(defproject chapter5 "0.1.0-SNAPSHOT"
  :jvm-ops ["-Xmx1G"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [iota "1.1.2"]
                 [tesser.math "1.0.0"]
                 [tesser.core "1.0.0"]
                 [tesser.hadoop "1.0.1"]
                 [com.damballa/parkour "0.5.4"]

                 [incanter/incanter   "1.5.5"]]

  :resource-paths ["data"]
  :main ^:skip-aot chapter5.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :provided {:dependencies
                        [[org.apache.hadoop/hadoop-client "2.4.1"]
                         [org.apache.hadoop/hadoop-common "2.4.1"]
                         [org.slf4j/slf4j-api "1.6.1"]
                         [org.slf4j/slf4j-log4j12 "1.6.1"]
                         [log4j "1.2.17"]]}

            })
