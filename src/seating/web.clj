(ns seating.web
  (:use [clojure.java.io :only [file]]
        [compojure.core :only [defroutes GET POST]]
        [compojure.handler :only [site]]
        [compojure.route :only [files not-found]]
        org.httpkit.server)
  (:require [clojure.data.json :as json]
            [seating.model :as model])
  (:gen-class))


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



(defn send-json! [chan msg]
  "jsonify a message and then send it on the given channel"
  (send! chan (json/write-str msg)))


(defn send-client-html [req]
  "return a response containing the single-page app"
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (file "static/client.html")})



(defn connection-handler [req]
  (with-channel req chan

    (println "New client!")

    (send-json! chan model/chart)
    (model/add-update-watch chan (fn [event] (->> event (to-msg)
                                                        (send-json! chan))))
    (doseq [guest (model/arrivals-so-far)]
      (send-json! chan guest)
      (println "Announcing to client:" guest))


    (on-close chan (fn [status]
                     (println "channel closed: " status)
                     (model/remove-update-watch chan)))

    (on-receive chan (fn [msg]
                       (println "recieved data: " msg)
                       (let [[action guest] (from-msg msg)]
                         (if (= :disarrived action)
                           (model/disarrive! guest)
                           (model/arrive! guest)))))))







(defroutes the-routes
  (GET "/" [] send-client-html)
  (GET "/live-feed" [] connection-handler)
  (files "/" {:root "static"})
  (not-found "<p> No such page... </p>"))

(defn -main
  "ENTRY POINT!"
  [& args]
  (println "STARTING UP!")
  (run-server (site #'the-routes) {:port 5555}))

