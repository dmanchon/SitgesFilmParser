(ns sitges.sqlite
  (:require [clojure.contrib.sql :as sql]))

(def db {
	 :classname "org.sqlite.JDBC"
	 :subprotocol "sqlite"
	 :subname "db/sitges.sqlite3"
	 })

(def new-db-conn (merge db {:create true}))

(defn create-tables []
  (do
    (sql/create-table
     :movies
     [:movie_id :integer "PRIMARY KEY"]
     [:title "varchar(128)" ]
     [:director "varchar(128)"]
     [:country "varchar(128)"])
    (sql/create-table
     :shows
     [:show_id :integer "PRIMARY KEY"]
     [:place "varchar(64)"]
     [:start :datetime]
     [:length :int])
    (sql/create-table
     :pass
     [:show_id :integer]
     [:movie_id :integer]
     ["PRIMARY KEY" "(show_id,movie_id)"])))

(defn create-db  []
  "Creates a new database"
  (sql/with-connection new-db-conn
    (create-tables)))

(defn add-movie [movie]
  (sql/with-connection db (sql/update-or-insert-values
			   :movies
			   ["movie_id=?" (:id movie)] 
			   {:movie_id (:id movie)
			    :title (:title movie)
			    :country (:country movie)
			    :director (:director movie)})))

(defn get-sql 
  "Generic select statement functionality"
  [sql-stmt]
  (sql/with-connection db
    (sql/with-query-results results sql-stmt
      (doall results))))

(defn get-movie-id [movie]
  (apply :movie_id (get-sql ["select movie_id from movies where title=?" (:title movie)])))

(defn get-show-id [show]
  (apply :show_id (get-sql ["select show_id from shows where place=? and start=?"
			    (:teather show)
			    (java.sql.Timestamp. (.getTime (:start-time show)))])))

(defn add-pass [movie show]
  (let [movie-id (get-movie-id movie)
	show-id (get-show-id show)]
    (sql/with-connection db (sql/update-or-insert-values
			     :pass
			     ["movie_id=? and show_id=?" movie-id show-id] 
			     {:movie_id movie-id
			      :show_id show-id}))))

(defn add-show [show]
  (let [start (java.sql.Timestamp. (.getTime (:start-time show)))]
    (sql/with-connection db (sql/update-or-insert-values
			     :shows
			     ["place=? and start=?" (:teather show) start] 
			     {:place (:teather show)
			      :start start
			      :length (:length show)}))))

(defn drop-tables []
  (do
    (sql/with-connection new-db-conn (sql/drop-table :movies))
    (sql/with-connection new-db-conn (sql/drop-table :shows))
    (sql/with-connection new-db-conn (sql/drop-table :pass))))

(defn get-movies []
  (get-sql ["select * from movies"]))

(defn get-shows [movie_id]
  (get-sql ["select * from shows where show_id in (select show_id from pass where movie_id=?)" movie_id]))