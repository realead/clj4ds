(ns chapter1.mystats
)

(defn mean
  [xs]
  (/ (apply + xs) (count xs))
)


;;slow O(nlogn) and only for count >=2
(defn median
   [xs]
   (let [sorted (sort xs)
         len    (count xs)
         middle (quot len 2)]
       (if (odd? len)
             (nth sorted middle)
             (mean [(nth sorted middle) (nth sorted (inc middle))])
       )
   )
)

(defn square
    [x]
    (* x x)
)

(defn variance
   [xs]
   (let [x-bar (mean xs)]
      (mean (map #(square (- % x-bar)) xs))
   )     
)

(defn standard-deviation
   [xs]
   (Math/sqrt (variance xs))  
)


;;slow O(nlogn)
(defn quantile
  [xs q]
  (let [n (count xs)
        i (int (* (dec n) q))]
        (nth (sort xs) i)
   )
)
