(ns sitges.route
  (:use [compojure.core]
	[ring.adapter.jetty])
  (:require [compojure.route :as route]
	    [net.cgrand.enlive-html :as html]
	    [compojure.handler :as handler]))

(defroutes main-routes
  (GET "/" [] "<html><h1>Hello</h1></html>")
  (route/files "/" {:root "public"})
  (route/resources "/")
  (route/not-found "Page not found"))

(def web-app
     (-> main-routes))

(def server (run-jetty #'web-app {:port 8080 :join? false}))
