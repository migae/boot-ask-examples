(def +project+ 'tmp.alexa/hellogae)
(def +version+ "0.1.0-SNAPSHOT")

(set-env!
 :asset-paths #{"resources/public"}
 :resource-paths #{"src/clj"}
 :source-paths #{"config"}

 :repositories #(conj % ["maven-central" {:url "http://mvnrepository.com"}]
                      ["central" "http://repo1.maven.org/maven2/"])

 :dependencies   '[[org.clojure/clojure "1.8.0" :scope "runtime"]
                   [org.clojure/tools.logging "0.3.1"]
                   [ch.qos.logback/logback-classic "1.1.3"]

                   [migae/boot-ask "0.1.0-SNAPSHOT" :scope "test"]

                   ;; if you want to implement as an AWS Lambda:
                   ;; [com.amazonaws/aws-lambda-java-core "1.0.0" :scope "compile"]
                   ;; [com.amazon.alexa/alexa-skills-kit "1.2" :scope "compile"]

                   ;; if you want to implement on GAE:
                   [migae/boot-gae "0.2.0-SNAPSHOT" :scope "test"]
                   [migae/alexa-skills-kit "1.2"]
                   [javax.servlet/servlet-api "2.5" :scope "provided"]

                   ;; this is for the GAE runtime (NB: scope provided):
                   [com.google.appengine/appengine-java-sdk RELEASE :scope "provided" :extension "zip"]

                   ;; ;; this is required for gae appstats (NB: scope runtime, not provided?):
                   [com.google.appengine/appengine-api-labs RELEASE :scope "runtime"]

                   ;; this is for the GAE services like datastore (NB: scope runtime):
                   ;; (required for appstats, which uses memcache)
                   [com.google.appengine/appengine-api-1.0-sdk RELEASE :scope "runtime"]

                   ;; [org.mobileink/migae.datastore "0.3.3-SNAPSHOT" :scope "runtime"]

                   [hiccup/hiccup "1.0.5"]
                   [cheshire/cheshire "5.7.0"]
                   [compojure/compojure "1.5.2"]
                   [ring/ring-core "1.5.1"]
                   [ring/ring-devel "1.5.1" :scope "test"]
                   [ring/ring-servlet "1.5.1"]
                   [ring/ring-defaults "0.2.1"]
                   [ns-tracker/ns-tracker "0.3.0"]
                   ])

(require '[migae.boot-ask :as ask]
         '[migae.boot-gae :as gae]
         '[boot.task.built-in :as boot])

(task-options!
 pom  {:project     +project+
       :version     +version+
       :description "ASK helloworld edmo"
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(def web-inf-dir "WEB-INF")
(def classes-dir (str web-inf-dir "/classes"))

#_(deftask deploy
  "build and deploy"
  []
  (build :prod true)
  (gae/deploy :build-dir "target"))

(deftask build
  "Configure and build Alexa Skill for GAE"
  [k keep bool "keep intermediate .clj and .edn files"
   p prod bool "production build, without reloader"
   s service bool "build a service"
   u unit-test bool "build for unit testing, otherwise for integration testing"
   v verbose bool "verbose"]
  (let [keep (or keep false)
        verbose (or verbose false)]
    (comp (gae/install-sdk)
          (gae/libs :verbose verbose)
          ;; (gae/appstats :keep keep :verbose verbose)
          (ask/security :test (if prod false true) :verbose verbose)
          (ask/speechlets :keep keep :verbose verbose)
          (ask/servlets :keep keep :verbose verbose)
	  (gae/servlets :keep keep :verbose verbose)
          (gae/logging :jul true :verbose verbose)
          (if prod identity (gae/reloader :unit-test unit-test :keep keep :verbose verbose))
          (gae/config-service :unit-test unit-test )
          (gae/build-sift :unit-test unit-test )
          #_(if servlet
            identity
            (gae/install-service))
          (if prod identity (gae/keep-config))
          (target)
          )))

