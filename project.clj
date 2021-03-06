(defproject seating "0.1.0-SNAPSHOT"
  :description "This is the interactive seating chart"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [compojure "1.3.1"]
                 [ring "1.2.1"]
                 [http-kit "2.1.16"]]
  :main ^:skip-aot seating.web
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
