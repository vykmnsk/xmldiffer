(ns xmlparser.core
  (:use [clojure.data.zip.xml :only (attr text xml-> xml1->)]
        [clojure.string :only (join)]
        [clojure.set :only (intersection)]
        )
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip :as dzip]
            [clojure.java.io :as io]
            )
  )

(defn parse-zip-xml[file-path]
  (zip/xml-zip (xml/parse file-path)))

(defn tag1[zipnode]
    (:tag (first zipnode)))

(defn find-by-tag [nodes, tag]
  (filter #(= tag (tag1 %)) nodes))

(defn find-1st-node-by-tag[nodes tag]
  (first (find-by-tag nodes tag)))

(defn remove-dupes [nodes]
  (let [unique-tags (set (map tag1 nodes))]
    (map #(find-1st-node-by-tag nodes %) unique-tags)
  )
 )

(defn missing[in compared-to]
  (filter #(not (contains? (set in) %)) (set compared-to)))

(defn in-both [nodes1 nodes2]
  (let [tags1 (set (map tag1 nodes1))
        tags2 (set (map tag1 nodes2))
        tags-in-both (intersection tags1 tags2)
        matching-nodes1 (map #(find-1st-node-by-tag nodes1 %) tags-in-both)
        matching-nodes2 (map #(find-1st-node-by-tag nodes2 %) tags-in-both)
        ]
    (map vector matching-nodes1 matching-nodes2)))

(defn node-info[node]
  (let [parent-nodes (dzip/ancestors node)
        parent-nodes-tags (map tag1 parent-nodes)
        child-nodes (dzip/children node)
        child-tag-nodes (remove-dupes (filter tag1 child-nodes))
        ]
    {:path (rest (reverse parent-nodes-tags))
     :child-tags (map tag1 child-tag-nodes)
     :child-nodes child-tag-nodes}))

(defn diff-node-pair [node1 node2]
  (let [
        path1 (:path (node-info node1))
        path2 (:path (node-info node2))
        child-nodes1 (:child-nodes (node-info node1))
        child-nodes2 (:child-nodes (node-info node2))
        child-tags1 (:child-tags (node-info node1))
        child-tags2 (:child-tags (node-info node2))
        missing-in1 (missing child-tags1 child-tags2)
        missing-in2 (missing child-tags2 child-tags1)
        ]
       (println path1 " 1-> " child-tags1)
       (println path2 " 2-> " child-tags2)
      (if-not (empty? missing-in1)
        (println path1 " missing in 1: " missing-in1))
      (if-not (empty? missing-in2)
        (println path2 " missing in 2: " missing-in2))

  (let [child-nodes (in-both child-nodes1 child-nodes2)]
    (doseq [pair child-nodes]
      (diff-node-pair (first pair) (second pair)))))
 )


(defn -main [& files]
  (if-not (= 2 (count files))
    ((println "Must have 2 params: file1 file2")
    (System/exit 0)))

  (let [zip1 (parse-zip-xml (first files))
        zip2 (parse-zip-xml (second files))]

      (diff-node-pair zip1 zip2)
    )
)

 ; (with-open [wrtr (io/writer "mismatches.log")]
  ;   (doseq [m mismatches]
  ;     (.write wrtr (str m "\n"))
  ;     (println m)))

