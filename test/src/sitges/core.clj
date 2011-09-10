(ns sitges.core
  (:require [net.cgrand.enlive-html :as html]
	    [clojure.contrib.sql :as sql]
	    [clojure.contrib.string :as str]))

(def *base-url* "http://sitgesfilmfestival.com/cat/programa/pel_licules/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-movie-links []
  (let [selector [:td.negro (html/attr? :href)]
	filter #(:href (:attrs %))]
    (map filter (html/select (fetch-url *base-url*) selector))))

(defrecord Movie [id title])

(defn get-movie-title [page]
  (let [ selector [:table.parrilla3 :a]
	filter #(html/text %)]
    (first (map filter (html/select page selector)))))

(defn get-movie-id [link]
  (str/drop (- (.length link) 8) link))

(defn get-movie-data [url]
  (let [page (fetch-url url)]
    (Movie. (get-movie-id url)
	    (get-movie-title page))
    ))

(defn -main [& args]
  (do
    (println "Festival Internacional de Cinema de Sitges 2011")
    (doseq [link (get-movie-links)] (println (get-movie-data link)))))




(def db {
	 :classname "org.sqlite.JDBC"
	 :subprotocol "sqlite"
	 :subname "db/sitges.sqlite3"
	 })

(def new-db-conn (merge db {:create true}))

(defn create-tables []
  (sql/create-table
   :movies
   [:id :integer "PRIMARY KEY"]
   [:title "varchar(128)"]))

(defn create-db  []
  "Creates a new database"
  (sql/with-connection new-db-conn
    (create-tables)))


