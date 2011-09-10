(ns sitges.core
  (:gen-class)
  (:import [java.util.Date]
	   [java.net.URL])
  (:use clojure.contrib.command-line)
  (:require [net.cgrand.enlive-html :as html]
	    [sitges.parser :as parse]
	    [sitges.sqlite :as sql]
	    [clojure.contrib.string :as str]))

(def *base-url* "http://sitgesfilmfestival.com/cat/programa/pel_licules/")

(defrecord Movie [id
		  title
		  director
		  country
		  show])

(defrecord Shows [teather
		 start-time
		 length
		 movies])

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn download-show [page]
  (map #(Shows. %1 %2 %3 %4)
       (parse/teather page)
       (parse/date page)
       (parse/length page)
       (parse/movies page)))
  
(defn download-movie-data [url]
  (let [page (fetch-url url)]
    (Movie. (parse/id url)
	    (parse/title page)
	    (parse/director page)
	    (parse/country page)
	    (download-show page))))

(defn add-to-db [movie]
  (do 
    (sql/add-movie movie)
    (doseq [show (:show movie)] (do
				  (sql/add-show show)
				  (sql/add-pass movie show)))))
(defn get-movies []
  (let [movies (sql/get-movies)]
    (map #(Movie. (:movie_id %)
		  (:title %)
		  (:director %)
		  (:country %)
		  nil) movies)))

(defn get-shows [movie_id]
  (let [shows (sql/get-shows movie_id)]
    (map #(Shows. (:place %)
		  (:start %)
		  (:length %)
		  nil) shows)))

(defn -main [& args]
  (with-command-line args
    "Tool to download and manipulate database of sitges festival movies."
    [[show? s? "Prints databse"]
     [populate? p? "Download movie information and populate database."]
     [drop? d? "Drops database and create a new one."]
     remaining]
    (println "Festival Internacional de Cinema de Sitges 2011")
    (if (= drop? true)
      (do
	(sql/drop-tables)
	(sql/create-db)))

    (if (= populate? true)
      (doseq [link (parse/links (fetch-url *base-url*))]
	(do
	  (let [movie (download-movie-data link)]
	    (add-to-db movie)))))

    (if (= show? true)
      (doseq [movie (get-movies)]
      	    (println (:title movie) " | "
		     (:country movie) " | "
		     (:director movie) " | "
		     (map :teather (get-shows (:id movie))))))))

