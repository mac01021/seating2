(ns seating.model
  (:require [seating.db :as db]))


(def chart (db/get-saved-chart))

(def state (atom {:socks #{}
                  :arrivals (db/get-saved-arrivals)
                  :last-update nil}))

(defn connect
  [srv client]
  (-> srv
      (update-in [:socks] conj client)
      (assoc-in [:last-update] [:connected client])))

(defn disconnect
  [srv client]
  (-> srv
      (update-in [:socks] disj client)
      (assoc-in [:last-update] [:disconnected client])))

(defn arrive
  [srv guest]
  (-> srv
      (update-in [:arrivals] conj guest)
      (assoc-in [:last-update] [:arrived guest])))

(defn disarrive
  [srv guest]
  (-> srv
      (update-in [:arrivals] disj guest)
      (assoc-in [:last-update] [:disarrived guest])))





;;protocol stuff
(defn to-msg [[action guest]]
  (cond
    (= action :arrived) guest
    (= action :disarrived) (str "!" guest)
    :else nil))


(defn from-msg [msg]
  (cond
    (= (first msg) \!) [:disarrived (subs msg 1)]
    :else              [:arrived msg]))


(defn choose-request-handler  [msg]
  (let [[action guest] (from-msg msg)]
    (if (= action :disarrived)
      #(disarrive %1 guest)
      #(arrive %1 guest))))

