(ns chapter2.core
  (:require [chapter2.data :refer :all]
            [chapter2.examples :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls])
  (:gen-class))



(def examples [
 ;                      ex-2-1 
 ;                      ex-2-2
 ;                      ex-2-3
 ;                      ex-2-4
 ;                      ex-2-5
 ;                      ex-2-6
 ;                     ex-2-7
                        ex-2-1011
              ]
)

(def examples-with-data [
 ;                     ex-2-8
 ;                     ex-2-9
              ]
)


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (doseq  [f examples] 
      (-> (f)
          (pr-str)
          (println)
      )
  )

  (let [data (load-data "dwell-times.tsv")]
      (doseq  [f examples-with-data] 
          (-> (f data)
              (pr-str)
              (println)
          )
      )
  )
)
