(ns chapter5.core
  (:require [chapter5.data :refer :all]
            [chapter5.examples :refer :all])
  (:gen-class))



(def examples [
 ;               ex-5-1
 ;              ex-5-2
 ;              ex-5-3
 ;              ex-5-4
                ex-5-5
                ex-5-6
              ]
)

(def examples-with-data [
 
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

;  (let [data (titanic-data)]
;      (doseq  [f examples-with-data] 
;          (-> (f data)
;              (pr-str)
;              (println)
;          )
;      )
;  )
)
