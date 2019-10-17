(ns chapter5.core
  (:require [chapter5.data :refer :all]
            [chapter5.examples :refer :all])
  (:gen-class))



(def examples [
 ;               ex-5-1
 ;              ex-5-2
 ;              ex-5-3
 ;              ex-5-4
 ;              ex-5-5
 ;              ex-5-6
 ;              ex-5-7
 ;              ex-5-8
 ;              ex-5-9
 ;             ex-5-18
 ;             ex-5-19
 ;            ex-5-20
 ;             ex-5-21
 ;             ex-5-22
 ;             ex-5-23
 ;             ex-5-24
 ;             ex-5-25
 ;             ex-5-26
 ;             ex-5-27
 ;             ex-5-28
 ;             ex-5-29
 ;             ex-5-30
 ;             ex-5-31
               ex-5-32
              ]
)

(def examples-with-data [
 ;               ex-5-10
 ;              ex-5-11
 ;              ex-5-12
 ;              ex-5-13
 ;              ex-5-14
 ;              ex-5-15
 ;              ex-5-16
 ;              ex-5-17
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

  (let [data (soi-data)]
      (doseq  [f examples-with-data] 
          (-> (f data)
              (pr-str)
              (println)
          )
      )
  )
)
