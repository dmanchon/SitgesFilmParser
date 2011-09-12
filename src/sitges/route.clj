(ns sitges.route
  (:use [compojure.core]
	[ring.adapter.jetty])
  (:require [compojure.route :as route]
	    [net.cgrand.enlive-html :as html]
	    [compojure.handler :as handler]))



(html/deftemplate index "sitges/template.html"
  [ctxt]
  [:p#message] (html/content (:message ctxt)))


(defroutes main-routes
  (GET "/" [] (index {}))
  (GET "/change/:msg" [msg] (index {:message (str "we change the message to " msg)}))
  (route/files "/" {:root "resources/www"})
  (route/resources "/")
  (route/not-found "Page not found"))

(def web-app
     (-> main-routes))

(defn start-server []
  (do
    (defonce server (run-jetty #'web-app {:port 8080 :join? false}))
    (.start server)))

(defn stop-server []
  (.stop server))