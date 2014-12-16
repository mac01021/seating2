(ns seating.web
  (:use [clojure.java.io :only [file]]
        [compojure.core :only [defroutes GET POST]]
        [compojure.handler :only [site]]
        [compojure.route :only [files not-found]]
        org.httpkit.server)
  (:require [clojure.data.json :as json]
            [seating.model :as model])
  (:gen-class))



(defn send-json! [chan msg]
  (send! chan (json/write-str msg)))



(defn send-client-html [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (file "static/client.html")})



(defn connection-handler [req]
  (with-channel req chan

    (println "New client!")

    (send-json! chan model/chart)

    (let [{:keys [arrivals]} (swap! model/state model/connect chan)]
       (doseq [guest arrivals]
          (send-json! chan guest)
          (println "Announcing to client:" guest)))

    (on-close chan (fn [status]
                     (println "channel closed: " status)
                     (swap! model/state model/disconnect chan)))

    (on-receive chan (fn [msg]
                       (println "recieved data: " msg)
                       (swap! model/state (model/choose-request-handler msg))))))



(defn broadcast [k r old new]
  (let [{:keys [socks last-update]} new
        msg (model/to-msg last-update)]
    (when-not (nil? msg)
      (doseq [sock socks]
        (send-json! sock msg)))))

(add-watch model/state :monitor broadcast)



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

