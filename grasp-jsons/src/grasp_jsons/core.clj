(ns grasp-jsons.core
  (:gen-class)
  (:require [cheshire.core :refer [parse-string]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn read-json
  [path]
  (parse-string (slurp path)))

(def id-data (read-json "../data/podaci_o_srednjim_skolama.json"))

(defn schools-wiht-students-home
  []
  (filter #(some #{"1" "2"} [(get % "vrsta_id")]) id-data))

(take 3 (schools-wiht-students-home))
