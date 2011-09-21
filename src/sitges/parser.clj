(ns sitges.parser
  (:import [java.util Date])
  (:require [net.cgrand.enlive-html :as html]
	    [clojure.contrib.string :as str]))

(comment
  There is a lot of cut&paste in here, but i was writing it in a rush.
  I will refactor and pack everything some day.
  )

(defn links [page]
  (let [selector [:td.negro (html/attr? :href)]
	filter #(:href (:attrs %))]
    (map filter (html/select page selector))))

(defn title [page]
  (let [selector [:head :title]
	filter #(html/text %)
	len-title 23]
    (str/drop len-title (first (map filter (html/select page selector))))))

(defn id [link]
  (Integer. (str/drop (- (.length link) 8) link)))

(defn director [page]
  (let [selector [:a.links_inicio_contenido]
	filter #(html/text %)]
    (first (map filter (html/select page selector)))))
  
(defn country [page]
  (let [selector [:p :strong]
	filter #(html/text %)]
    (when-let [s (first (map filter (html/select page selector)))]
      (str/rtrim (first (str/split #"\." s ))))))

(defn teather [page]
  (let [selector [:tr.table_tr_1]
	filter #(html/text %)
	shows (map #(map filter (html/select % [:td])) (html/select page selector))]
    (map #(nth % 2) shows)))

(defn movies [page]
  (let [selector [:ul.ul_llista]
	filter #(html/text %)
	shows (map #(map filter (html/select % [:li :a])) (html/select page selector))]
    shows))


(defn day [page]
  (let [selector [:tr.table_tr_1]
	filter #(html/text %)
	shows (map #(map filter (html/select % [:td])) (html/select page selector))]
    (map #(str/replace-re #"[\t\n]" "" %) (map #(nth % 0) shows))))

(defn hour [page]
  (let [selector [:tr.table_tr_1]
	filter #(html/text %)
	shows (map #(map filter (html/select % [:td])) (html/select page selector))]
    (map #(nth % 1) shows)))

(defn length [page]
  (let [selector [:td.negro_no_b]
	filter #(html/text %)
	shows (map filter (html/select page selector))]
    (map #(str/replace-re #"[\t\n]" "" %) shows)))

(defn date [page]
  (let [year 2011
	day (map #(str/split #"\/" % ) (day page))
	hour (map #(str/split #":" %) (hour page))
	length (length page)]
    (when (= (count day) (count hour) (count length))
      (let [shows (apply map list (list day hour length))]
	(map #(Date. year
		     (dec (Integer. (second (first %))))
		     (Integer. (first (first %)))
		     (Integer. (first (second %)))
		     (Integer. (second (second %)))) shows)))))