(ns seating.model
  (:require [seating.db :as db]))


(def chart (db/get-saved-chart))

(def state (atom {:arrivals (db/get-saved-arrivals)
                  :last-update nil}))


(defn arrive
  [srv guest]
  (-> srv
      (update-in [:arrivals] conj guest)
      (assoc-in [:last-update] [:arrived guest])))

(defn arrive! [guest]
  (swap! state arrive guest))

(defn disarrive
  [srv guest]
  (-> srv
      (update-in [:arrivals] disj guest)
      (assoc-in [:last-update] [:disarrived guest])))

(defn disarrive! [guest]
  (swap! state disarrive guest))

(defn add-update-watch [k f]
  "Pass in a key and a function that takes a 2-vector (action, guest)"
  (let [wf (fn [k r olds news] (f (:last-update news)))]
    (add-watch state k wf)))

(defn remove-update-watch [k]
  (remove-watch state k))

(defn arrivals-so-far [] (:arrivals @state))





