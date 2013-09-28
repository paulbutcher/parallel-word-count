(defproject fp-wordcount "1.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.xml "0.0.7"]]
  :main wordcount.core
  :jvm-opts ^:replace ["-Xms4G" "-Xmx4G" "-XX:NewRatio=8" "-server"])
