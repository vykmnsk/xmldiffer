(defproject xmldiffer "0.1.0-SNAPSHOT"
  :description "Find differences between two XML files using XMLUnit"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [xmlunit/xmlunit "1.5"]]
  :aot  [xmldiffer.core]
  :main xmldiffer.core
  )
