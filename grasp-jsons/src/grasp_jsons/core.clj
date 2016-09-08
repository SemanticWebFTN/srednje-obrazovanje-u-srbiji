(ns grasp-jsons.core
  (:gen-class)
  (:require [cheshire.core :refer [parse-string]]
            [clojure.string :as s]))

(defn read-json
  [path]
  (parse-string (slurp path)))

(def id-data (read-json "../data/podaci_o_srednjim_skolama.json"))
(def edu-profiles (read-json "../data/obrazovni_profili_srednje_skole.json"))

(defn parse
  "Parse string of integers separated with a comma to vector of integers."
  [string & {:keys [one]}]
  (let [nums (map #(Integer/parseInt (s/trim %)) (filter not-empty
                                                         (s/split string #",")))]
    (if one (first nums) nums)))

(defn schools-wiht-students-home []
  (filter #(contains? #{"5" "6"} (get % "vrsta_id")) id-data))

(defn competencies-by-id [ids]
  (filter #(contains? (set (map str ids)) (get % "ss_smer_id"))
          edu-profiles))

(defn competency-by-school
  "Retrieve map of schools mapped to competencies they offer.
  `school' key is key to represent school, whole if nil.
  `competency' is in the same manner.
  `schools' are schools to be considered."
  [& {:keys [school competency schools]}]
  (map (fn [sch]
         (let [competencies (-> (get sch "smer") parse competencies-by-id)]
           [(if school (get sch school) sch)
            (map #(if competency (get % competency) %) competencies)]))
       (or schools id-data)))

(defn durations-to-schools
  "Retrieve durations mapped to schools.
  `school' key is key to represent school.
  `schools' are schools to be considered."
  [& {:keys [school schools]}]
  (reduce (fn [acum [sch competencies]]
            (let [sch-repr (if school (get sch school) sch)]
              (loop [cs competencies ac acum]
                (if (not-empty cs)
                  (let [dur (parse (get (first cs) "trajanje") :one true)]
                    (recur (rest cs)
                           (merge-with concat ac
                                       {dur
                                        (when (not-any? #{sch-repr} (get ac dur))
                                          [sch-repr])})))
                  ac))))
          {}
          (competency-by-school :schools schools)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; ;; Three year program with student's home
;; (get (durations-to-schools :school "naziv_skole"
;;                            :schools (schools-wiht-students-home))
;;      3)
