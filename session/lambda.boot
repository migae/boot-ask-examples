;; lambda.boot
(def +project+ 'tmp.alexa.lambda/spacegeek)
(def +version+ "0.1.0-SNAPSHOT")

(set-env!
 :gae {:app {:id "alexa-skills-kit"
             :dir "./"}
       :module {:name "helloworld"
                :version "v1"}}

 :asset-paths #{"resources/public"}
 :resource-paths #{"src/clj"}
 :source-paths #{"config"}

 :repositories #(conj % ["maven-central" {:url "http://mvnrepository.com"}]
                      ["central" "http://repo1.maven.org/maven2/"])

 :dependencies   '[[org.clojure/clojure "1.8.0" :scope "runtime"]
                   [org.clojure/tools.logging "0.3.1"]

                   [migae/boot-ask "0.1.0-SNAPSHOT" :scope "test"]
                   [migae/alexa-skills-kit "1.2"]
                   ;;[com.amazon.alexa/alexa-skills-kit "1.2" :scope "compile"]

                   [com.amazonaws/aws-lambda-java-core "1.0.0" :scope "compile"]
                   ;; [com.amazonaws/aws-lambda-java-events "1.3.0"]
                   [com.amazonaws/aws-lambda-java-log4j "1.0.0"]
                   ])

(require '[migae.boot-ask :as ask]
         '[boot.task.built-in :as boot])

(task-options!
 ask/deploy-lambda {:project     +project+
                    :version     +version+}
 ask/lambda-update {:project     +project+
                    :version     +version+}
 pom  {:project     +project+
       :version     +version+
       :description "ASK helloworld demo"
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

#_(deftask deploy
  "build and deploy"
  []
  (build :prod true)
  (gae/deploy :build-dir "target"))

(deftask cc
  "Configure and build servlet or service app"
  []
  (let [aotns 'hello.speechlet.handler]
    (aot :namespace #{aotns})))

(deftask build
  "Configure and build servlet or service app"
  [k keep bool "keep intermediate .clj and .edn files"
   p prod bool "production build, without reloader"
   s service bool "build a service"
   v verbose bool "verbose"]
  (let [keep (or keep false)
        verbose (or verbose false)]
    (comp (ask/speechlets :keep keep :verbose verbose)
          (ask/lambdas :keep keep :verbose verbose)
          ;; (cc)
          ;;(ask/keep-config)
          (ask/deploy-lambda))))

