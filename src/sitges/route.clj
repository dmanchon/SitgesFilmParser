(ns sitges.route
  (:use [compojure.core]
	[ring.adapter.jetty])
  (:require [compojure.route :as route]
	    [net.cgrand.enlive-html :as html]
	    [compojure.handler :as handler]))



(html/deftemplate index "sitges/template.html"
  [ctxt]
  [:p#message] (html/content (:message ctxt)))

(def *dummy-context*
     {:title "Enlive Template2 Tutorial"
      :sections [{:title "Clojure"
                  :links [{:text "Macros"
                           :href "http://www.clojure.org/macros"}
                          {:text "Multimethods & Hierarchies"
                           :href "http://www.clojure.org/multimethods"}]}
                 {:title "Compojure"
                  :links [{:text "Requests"
                           :href "http://www.compojure.org/docs/requests"}
                          {:text "Middleware"
                           :href "http://www.compojure.org/docs/middleware"}]}
                 {:title "Clojars"
                  :links [{:text "Clutch"
                           :href "http://clojars.org/org.clojars.ato/clutch"}
                          {:text "JOGL2"
                           :href "http://clojars.org/jogl2"}]}
                 {:title "Enlive"
                  :links [{:text "Getting Started"
                           :href "http://wiki.github.com/cgrand/enlive/getting-started"}
                          {:text "Syntax"
                           :href "http://enlive.cgrand.net/syntax.html"}]}]})

(def *link-sel* [[:.content (html/nth-of-type 1)] :> (html/nth-child 1)])

(html/defsnippet link-model "sitges/template2.html" *link-sel*
  [{text :text href :href}] 
  [:a] (html/do-> 
        (html/content text) 
        (html/set-attr :href href)))

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