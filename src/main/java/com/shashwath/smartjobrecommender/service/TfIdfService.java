package com.shashwath.smartjobrecommender.service;

import com.shashwath.smartjobrecommender.model.Job;
import com.shashwath.smartjobrecommender.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TfIdfService {

    private final JobRepository jobRepository;

    public TfIdfService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

  
    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();

        if (text == null) return tokens;

        text = text.toLowerCase();
        String[] words = text.split("\\W+");

        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 1) { // avoid 1-letter tokens
                tokens.add(words[i]);
            }
        }

        return tokens;
    }

    
    private List<Map<String, Integer>> buildTermFreqs(List<String> docs) {
        List<Map<String, Integer>> tfList = new ArrayList<>();

        for (int i = 0; i < docs.size(); i++) {
            List<String> tokens = tokenize(docs.get(i));
            Map<String, Integer> tf = new HashMap<>();

            for (int j = 0; j < tokens.size(); j++) {
                String word = tokens.get(j);
                if (tf.containsKey(word)) {
                    tf.put(word, tf.get(word) + 1);
                } else {
                    tf.put(word, 1);
                }
            }

            tfList.add(tf);
        }

        return tfList;
    }

   
    private Map<String, Double> buildIdf(List<Map<String, Integer>> tfList) {
        Map<String, Integer> df = new HashMap<>();

        // Count in how many docs each word appears
        for (int i = 0; i < tfList.size(); i++) {
            Map<String, Integer> tf = tfList.get(i);
            for (String word : tf.keySet()) {
                if (df.containsKey(word)) {
                    df.put(word, df.get(word) + 1);
                } else {
                    df.put(word, 1);
                }
            }
        }

        int N = tfList.size();
        Map<String, Double> idf = new HashMap<>();

        // Smoothing: log((N+1)/(df+1)) + 1
        for (String word : df.keySet()) {
            double smoothIdf = Math.log((N + 1.0) / (df.get(word) + 1.0)) + 1.0;
            idf.put(word, smoothIdf);
        }

        return idf;
    }

   
    private Map<String, Double> tfIdfVector(Map<String, Integer> tf, Map<String, Double> idf) {
        Map<String, Double> vector = new HashMap<>();

        int totalTerms = 0;
        for (Integer count : tf.values()) {
            totalTerms += count;
        }

        for (String word : tf.keySet()) {
            double tfValue = (double) tf.get(word) / totalTerms;
            double idfValue = idf.getOrDefault(word, 0.0);

            vector.put(word, tfValue * idfValue);
        }

        return vector;
    }

    
    private double cosine(Map<String, Double> v1, Map<String, Double> v2) {
        double dot = 0.0;
        double mag1 = 0.0;
        double mag2 = 0.0;

        for (String word : v1.keySet()) {
            double val1 = v1.get(word);
            mag1 += val1 * val1;

            if (v2.containsKey(word)) {
                dot += val1 * v2.get(word);
            }
        }

        for (double val : v2.values()) {
            mag2 += val * val;
        }

        if (mag1 == 0.0 || mag2 == 0.0) {
            return 0.0;
        }

        return dot / (Math.sqrt(mag1) * Math.sqrt(mag2));
    }

    
    public List<ScoredJob> recommend(String query, int limit) {

        List<Job> jobs = jobRepository.findAll();

        // Step 1: Prepare job docs
        List<String> docs = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            String doc = job.getTitle() + " " + job.getDescription() + " " + job.getSkills();
            docs.add(doc);
        }

        // Step 2: TF for all jobs
        List<Map<String, Integer>> tfs = buildTermFreqs(docs);

        // Step 3: IDF
        Map<String, Double> idf = buildIdf(tfs);

        // Step 4: TF-IDF vectors for jobs
        List<Map<String, Double>> jobVecs = new ArrayList<>();
        for (int i = 0; i < tfs.size(); i++) {
            jobVecs.add(tfIdfVector(tfs.get(i), idf));
        }

        // Step 5: Query vector
        List<String> qList = new ArrayList<>();
        qList.add(query);
        Map<String, Integer> qTf = buildTermFreqs(qList).get(0);
        Map<String, Double> qVec = tfIdfVector(qTf, idf);

        // Step 6: Score jobs
        List<ScoredJob> scored = new ArrayList<>();

        for (int i = 0; i < jobs.size(); i++) {
            double score = cosine(qVec, jobVecs.get(i));

            // ⛔ DON’T SHOW JOBS WITH SCORE = 0.0
            if (score > 0.05) {
                scored.add(new ScoredJob(jobs.get(i), score));
            }
        }

        // Step 7: SORT manually
        for (int i = 0; i < scored.size() - 1; i++) {
            for (int j = 0; j < scored.size() - i - 1; j++) {
                if (scored.get(j).score < scored.get(j + 1).score) {
                    ScoredJob temp = scored.get(j);
                    scored.set(j, scored.get(j + 1));
                    scored.set(j + 1, temp);
                }
            }
        }

        // Step 8: LIMIT results
        if (limit > scored.size()) limit = scored.size();

        List<ScoredJob> finalList = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            finalList.add(scored.get(i));
        	
        }

        return finalList;
    }

    
    public static class ScoredJob {
        public Job job;
        public double score;
        

        public ScoredJob(Job job, double score) {
            this.job = job;
            this.score = score;
        }
    }
}
