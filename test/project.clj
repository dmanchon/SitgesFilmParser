(defproject sitges "0.1.0"
  :description "Sitges festival parser"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [enlive "1.0.0-SNAPSHOT"]
		 [sqlitejdbc "0.5.6"]
                 [ring "0.2.5"]
                 [net.cgrand/moustache "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]]
  :source-path "src"
  :main sitges.core)
