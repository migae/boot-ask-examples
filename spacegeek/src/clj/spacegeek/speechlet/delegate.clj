(ns spacegeek.speechlet.delegate
  "gen-class :implements com.amazon.speech.speechlet.Speechlet"
  ;;(:refer-clojure :exclude [read read-string])
  (:import [com.amazon.speech.slu Intent]
           [com.amazon.speech.speechlet
            IntentRequest
            LaunchRequest
            Session
            SessionEndedRequest
            SessionStartedRequest
            Speechlet
            SpeechletException
            SpeechletResponse]
           [com.amazon.speech.ui
            PlainTextOutputSpeech
            Reprompt
            SimpleCard]

           #_[com.google.appengine.api.datastore EntityNotFoundException]
           [java.io InputStream ByteArrayInputStream]
           [java.util Collections]
           [java.lang IllegalArgumentException RuntimeException])
  (:require [clojure.tools.logging :as log :refer [debug info]] ;; :trace, :warn, :error, :fatal
            [spacegeek.speechlet.intents :as intent]))

(def facts
  ["A year on Mercury is just 88 days long."
   "Despite being farther from the Sun, Venus experiences higher temperatures than Mercury."
   "Venus rotates counter-clockwise, possibly because of a collision in the past with an asteroid."
   "On Mars, the Sun appears about half the size as it does on Earth."
   "Earth is the only planet not named after a god."
   "Jupiter has the shortest day of all the planets."
   "The Milky Way galaxy will collide with the Andromeda Galaxy in about 5 billion years."
   "The Sun contains 99.86% of the mass in the Solar System."
   "The Sun is an almost perfect sphere."
   "A total solar eclipse can happen once every 1 to 2 years. This makes them a rare event."
   "Saturn radiates two and a half times more energy into space than it receives from the sun."
   "The temperature inside the Sun can reach 15 million degrees Celsius."
   "The Moon is moving approximately 3.8 cm away from our planet every year."])

(def speech
  {:cancel  "Goodbye, cancelling"
   :help    (str "You can ask Space Geek tell me a space fact, or, you can say exit. "
                 "What can I help you with?")
   :fact    "Here's your space fact: "
   :stop    "Goodbye, I'm stopping"
   :welcome "Welcome to Space Geek. You can say 'tell me a fact'."})

(def card
  {:space-geek "SpaceGeek"})

(defn get-welcome-response
  []
  (let [speechText (:welcome speech)
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle card (:goodbye card))
    (.setContent card speechText)
    (.setText speech speechText)
    (.setOutputSpeech reprompt speech)
    (SpeechletResponse/newAskResponse speech, reprompt, card)))

(defn get-new-fact-response
  []
  (let [fact-index (rand-int (count facts))
        factoid (nth facts fact-index)
        speechText (str (:fact speech) factoid)
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)]
    (.setTitle card (:space-geek card))
    (.setContent card speechText)
    (.setText speech speechText)
    (SpeechletResponse/newTellResponse speech card)))

(defn get-help-response []
  (let [speechText (:help speech)
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle card (:goodbye card))
    (.setContent card speechText)
    (.setText speech speechText)
    (.setOutputSpeech reprompt speech)
    (SpeechletResponse/newAskResponse speech, reprompt, card)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; com.amazon.speech.speechlet.Speechlet API

(defn speechlet-onSessionStarted
  [this, request, session]
  (log/info (format "onSessionStarted requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  ;; any initialization logic goes here
  )

(defn speechlet-onLaunch
  ^SpeechletResponse
  [this, ^LaunchRequest request, ^Session session]
  (log/info (format "onLaunch requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  (get-welcome-response))

(defn speechlet-onIntent
  ^SpeechletResponse
  [this, ^IntentRequest request, ^Session session]
  (log/info (format "onIntent requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  (let [intent (.getIntent request)
        intent-name (if (nil? intent) nil (.getName intent))]
    (log/info (format "Intent is %s" intent-name))
    (log/info (format "Help Intent is %s" intent/help))
    (log/info (format "Eq? %s" (= intent-name intent/help)))
    (cond
      (= intent-name intent/new-fact) (get-new-fact-response)

      (= intent-name intent/help) (get-help-response)

      (= intent-name intent/stop) (let [outputSpeech (PlainTextOutputSpeech.)]
                                    (.setText outputSpeech (:stop speech))
                                    (SpeechletResponse/newTellResponse outputSpeech))

      (= intent-name intent/cancel) (let [outputSpeech (PlainTextOutputSpeech.)]
                                      (.setText outputSpeech (:cancel speech))
                                      (SpeechletResponse/newTellResponse outputSpeech))

      :else (throw (SpeechletException. "Invalid Intent")))))

(defn speechlet-onSessionEnded
  [this, ^SessionEndedRequest request ^Session session]
  (log/info (format "onSessionEnded requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  )
