(defproject chapter5 "0.1.0-SNAPSHOT"
  :jvm-ops ["-Xmx1G"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [incanter/incanter   "1.5.5"]]
  :resource-paths ["data"]
  :main ^:skip-aot chapter5.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
