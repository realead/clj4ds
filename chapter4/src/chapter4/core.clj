(ns chapter4.core
  (:require [chapter4.data :refer :all]
            [chapter4.examples :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls])
  (:gen-class))



(def examples [
;                   ex-3-11                    
              ]
)

(def examples-with-data [
;                 ex-4-1
;                 ex-4-2
;                 ex-4-3
;                 ex-4-4
;                 ex-4-5
;                 ex-4-6
;                 ex-4-7
;                 ex-4-8
;                 ex-4-9
;                 ex-4-10
;                 ex-4-11
;                 ex-4-12
;                 ex-4-13
                  ex-4-14
                  ex-4-15
                  ex-4-16
                  ex-4-17
                  ex-4-18
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

  (let [data (titanic-data)]
      (doseq  [f examples-with-data] 
          (-> (f data)
              (pr-str)
              (println)
          )
      )
  )
)
