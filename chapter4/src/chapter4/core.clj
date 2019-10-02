(ns chapter4.core
  (:require [chapter4.data :refer :all]
            [chapter4.examples :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls])
  (:gen-class))



(def examples [
 ;                  ex-4-19 
 ;                 ex-4-20  
 ;               ex-4-3031                 
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
;                 ex-4-14
;                  ex-4-15
;                  ex-4-16
;                  ex-4-17
;                ex-4-18
;                ex-4-21
;                ex-4-22
;                ex-4-23
;                ex-4-24
;                ex-4-25
;               ex-4-26
;              ex-4-27
;              ex-4-28
;              ex-4-29
              ex-4-32
              ex-4-33
              ex-4-34
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
