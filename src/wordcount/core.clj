(ns wordcount.core
  (:require [wordcount.pages :refer :all]
            [wordcount.words :refer :all]
            [clojure.core.reducers :as r]
            [foldable-seq.core :refer [foldable-seq]]))

(defn frequencies-fold [words]
  (r/fold (partial merge-with +)
          (fn [counts word] (assoc counts word (inc (get counts word 0))))
          words))

(defn frequencies-pmap [words partition-size]
  (reduce (partial merge-with +) 
    (pmap frequencies 
      (partition-all partition-size words))))

(defn frequencies-partition-then-fold [words partition-size]
  (reduce (partial merge-with +)
    (map #(frequencies-fold (into [] %))
      (partition-all partition-size words))))

(defn count-words-sequential [pages]
  (frequencies (mapcat get-words pages)))

(defn count-words-fold [pages]
  (frequencies-fold (r/mapcat get-words (foldable-seq pages))))

(defn count-words-pmap [pages partition-size]
  (frequencies-pmap (mapcat get-words pages) partition-size))

(defn count-words-partition-then-fold [pages partition-size]
  (frequencies-partition-then-fold (mapcat get-words pages) partition-size))

(defn count-words-partitioned
  [pages partition-size]
  (->> (partition partition-size pages)
       (pmap #(reduce
               (fn [m page]
                 (merge-with + m (frequencies (get-words page))))
               {}
               %))
       (reduce (partial merge-with +))))

(defn count-words-pmap-merge [pages]
  (reduce (partial merge-with +)
    (pmap #(frequencies (get-words %)) pages)))

(defn -main [& args]
  (let [[page-count filename algorithm psize] args]
    (time
      (case algorithm
        "sequential" (count-words-sequential (get-pages (Integer. page-count) filename))
        "fold" (count-words-fold (get-pages (Integer. page-count) filename))
        "pmap" (count-words-pmap (get-pages (Integer. page-count) filename) (Integer. psize))
        "pthenf" (count-words-partition-then-fold (get-pages (Integer. page-count) filename) (Integer. psize))
        "partitioned" (count-words-partitioned (get-pages (Integer. page-count) filename) (Integer. psize))
        "pmap-merge" (count-words-pmap-merge (get-pages (Integer. page-count) filename)))))
  (shutdown-agents)
  nil)