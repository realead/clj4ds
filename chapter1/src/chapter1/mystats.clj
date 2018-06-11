(ns chapter1.mystats
)

(defn mean
  [xs]
  (/ (apply + xs) (count xs))
)

