(ns chapter3.core
  (:require [chapter3.data :refer :all]
            [chapter3.examples :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls])
  (:gen-class))



(def examples [
                     ex-3-11                    
              ]
)

(def examples-with-data [
                    ex-3-1
                    ex-3-2
                    ex-3-3
                    ex-3-4
                   ex-3-5
                   ex-3-6
                    ex-3-7
                    ex-3-8
                    ex-3-9
                    ex-3-10
                    ex-3-12
                   ex-3-13
                    ex-3-13b
                   ex-3-14
                    ex-3-15
                    ex-3-16
                   ex-3-17
                    ex-3-18
                    ex-3-19
                    ex-3-20
                    ex-3-2122
                    ex-3-23
                    ex-3-25
                    ex-3-26
                    ex-3-27
                    ex-3-28
                    ex-3-29
                    ex-3-30
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

  (let [data (athlete-data)]
      (doseq  [f examples-with-data] 
          (-> (f data)
              (pr-str)
              (println)
          )
      )
  )
)
