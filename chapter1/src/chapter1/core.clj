(ns chapter1.core
  (:require [chapter1.data :refer :all]
            [chapter1.examples :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls])
  (:gen-class))



(def examples [
                       ex-1-1 
                       ex-1-3 
                       ex-1-6 
                       ex-1-7
                       ex-1-8
                       ex-1-9
                       ex-1-14
                       ex-1-15
                       ex-1-16
                       ex-1-17
                       ex-1-18
                       ex-1-19
                       ex-1-20
                       ex-1-21
                       ex-1-22
                       ex-1-23
                       ex-1-24
                       ex-1-25
                       ex-1-27
                       ex-1-28
                       ex-1-29
                       ex-1-33
              ]
)

(def examples-with-ru [
                       ex-1-30
                       ex-1-31
                       ex-1-32
                       ex-1-35
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

  ;;load ru-data only once:
  (if (not (empty? examples-with-ru))
      (let [ru-data (load-data :ru-victors)]
        (doseq  [f examples-with-ru] 
          (-> (f ru-data)
              (pr-str)
              (println)
          )
        )
      )
   )
)
