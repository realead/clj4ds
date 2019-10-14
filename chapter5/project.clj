(defproject chapter5 "0.1.0-SNAPSHOT"
  :jvm-ops ["-Xmx1G"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [iota "1.1.2"]
                 [tesser.math "1.0.0"]
                 [tesser.core "1.0.0"]
                 [incanter/incanter   "1.5.5"]]
  :resource-paths ["data"]
  :main ^:skip-aot chapter5.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
