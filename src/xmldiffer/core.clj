(ns xmldiffer.core
  (:require [clojure.java.io :as io])
  (:import (org.custommonkey.xmlunit Diff DetailedDiff XMLUnit ElementNameQualifier ElementNameAndTextQualifier))
  (:import (org.custommonkey.xmlunit.examples MultiLevelElementNameAndTextQualifier RecursiveElementNameAndTextQualifier))
  (:gen-class :main true)
  )

(def REPORT_NAME "xmldiffer-report.txt")

(defn differ[files]
  (let [xml1 (slurp (first files))
        xml2 (slurp (second files)) ]
    (Diff. xml1 xml2)))

(defn detailed[diff]
  (let [ddiff (DetailedDiff. diff)]
    (.overrideElementQualifier ddiff (new RecursiveElementNameAndTextQualifier))
    ddiff ))

(defn unrecoverable[difference]
  (not (.isRecoverable difference)))

(defn -main [& files]
  (if-not (= 2 (count files))
    ((println "Must have 2 params: file1 file2")
    (System/exit 0)))

  (XMLUnit/setIgnoreComments (Boolean/TRUE))
  (XMLUnit/setIgnoreWhitespace (Boolean/TRUE))
  ; (XMLUnit/setCompareUnmatched (Boolean/FALSE))

  (let [diff (differ files)
        ddiff (detailed diff)
        differences-all (.getAllDifferences ddiff)
        differences (filter unrecoverable (seq differences-all)) ]
    (println "similar=" (.similar diff))
    (println "identical=" (.identical diff))
    (with-open [wrtr (io/writer REPORT_NAME)]
      (doseq [d differences]
        (println d)
        (.write wrtr (str d "\n"))
        )))
  )
