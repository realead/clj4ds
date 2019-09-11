(defproject chapter4 "0.1.0-SNAPSHOT"
  :jvm-ops ["-Xmx1G"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [incanter/incanter   "1.5.5"]
                 [clj-time "0.9.0"]]
  :resource-paths ["data"]
  :main ^:skip-aot chapter4.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
