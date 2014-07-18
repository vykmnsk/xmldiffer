(defproject xmldiffer "0.1.0-SNAPSHOT"
  :description "Test parsing xml"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/data.xml "0.0.7"]
                 [com.uswitch/clj-soap "0.2.3"]
                 [xmlunit/xmlunit "1.5"]]
  :main xmldiffer.core)
