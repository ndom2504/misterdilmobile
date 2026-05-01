package com.example.misterdil.utils

/**
 * Calculateur CRS (Comprehensive Ranking System) simplifié pour Entrée Express
 * 
 * Ce calculateur fournit une estimation du score CRS basée sur les données du formulaire.
 * Le système CRS officiel est complexe et prend en compte de nombreux facteurs.
 * Cette version est une approximation pour donner une idée générale au client.
 */
object CRSCalculator {

    data class CRSResult(
        val totalScore: Int,
        val breakdown: Map<String, Int>,
        val program: String,
        val eligibility: EligibilityStatus
    )

    enum class EligibilityStatus {
        ELIGIBLE,
        NEEDS_IMPROVEMENT,
        NOT_ELIGIBLE
    }

    /**
     * Calcule le score CRS estimé à partir des données du formulaire
     */
    fun calculateCRS(formData: Map<String, String>): CRSResult {
        val scores = mutableMapOf<String, Int>()
        
        // 1. Âge (max 12 points)
        val ageScore = calculateAgeScore(formData["birth_date"])
        scores["Âge"] = ageScore
        
        // 2. Éducation (max 25 points)
        val educationScore = calculateEducationScore(formData["education_level"])
        scores["Éducation"] = educationScore
        
        // 3. Langue principale (max 24 points par compétence)
        val languageScore = calculateLanguageScore(
            formData["listening_score"]?.toIntOrNull() ?: 0,
            formData["speaking_score"]?.toIntOrNull() ?: 0,
            formData["reading_score"]?.toIntOrNull() ?: 0,
            formData["writing_score"]?.toIntOrNull() ?: 0
        )
        scores["Langue"] = languageScore
        
        // 4. Expérience de travail (max 15 points)
        val workExperienceScore = calculateWorkExperienceScore(
            formData["current_experience_years"]?.toIntOrNull() ?: 0,
            formData["has_canadian_experience"] == "true"
        )
        scores["Expérience"] = workExperienceScore
        
        // 5. Facteurs additionnels (max 600 points)
        val additionalScore = calculateAdditionalScore(
            formData["has_job_offer"] == "true",
            formData["has_provincial_nomination"] == "true",
            formData["studied_in_canada"] == "true",
            formData["family_in_canada"] == "true"
        )
        scores["Facteurs additionnels"] = additionalScore
        
        val totalScore = scores.values.sum()
        
        // Déterminer le programme probable
        val program = determineProgram(
            educationScore,
            workExperienceScore,
            languageScore,
            additionalScore
        )
        
        // Déterminer l'éligibilité
        val eligibility = determineEligibility(totalScore, languageScore, educationScore)
        
        return CRSResult(totalScore, scores, program, eligibility)
    }

    private fun calculateAgeScore(birthDate: String?): Int {
        // Simplification: score basé sur l'année de naissance
        // Dans une vraie implémentation, on calculerait l'âge exact
        val year = birthDate?.takeLast(4)?.toIntOrNull() ?: 2000
        val currentYear = 2026
        val age = currentYear - year
        
        return when {
            age in 20..29 -> 12
            age in 30..34 -> 10
            age in 35..39 -> 8
            age in 40..44 -> 5
            else -> 0
        }
    }

    private fun calculateEducationScore(educationLevel: String?): Int {
        return when (educationLevel) {
            "Doctorat" -> 25
            "Master" -> 23
            "Licence" -> 20
            "Baccalauréat" -> 15
            "BTS/DUT" -> 10
            else -> 5
        }
    }

    private fun calculateLanguageScore(
        listening: Int,
        speaking: Int,
        reading: Int,
        writing: Int
    ): Int {
        // Simplification: score basé sur la moyenne des 4 compétences
        // Dans le système réel, chaque compétence a son propre score
        val average = (listening + speaking + reading + writing) / 4.0
        
        return when {
            average >= 8.0 -> 24
            average >= 7.0 -> 20
            average >= 6.0 -> 16
            average >= 5.0 -> 12
            else -> 8
        }
    }

    private fun calculateWorkExperienceScore(
        years: Int,
        hasCanadianExperience: Boolean
    ): Int {
        val baseScore = when {
            years >= 5 -> 15
            years >= 3 -> 12
            years >= 1 -> 8
            else -> 0
        }
        
        val canadianBonus = if (hasCanadianExperience) 5 else 0
        
        return baseScore + canadianBonus
    }

    private fun calculateAdditionalScore(
        hasJobOffer: Boolean,
        hasProvincialNomination: Boolean,
        studiedInCanada: Boolean,
        familyInCanada: Boolean
    ): Int {
        var score = 0
        
        // Offre d'emploi valide
        if (hasJobOffer) score += 50
        
        // Nomination provinciale (600 points automatiquement)
        if (hasProvincialNomination) score += 600
        
        // Études au Canada
        if (studiedInCanada) score += 15
        
        // Famille au Canada
        if (familyInCanada) score += 5
        
        return score
    }

    private fun determineProgram(
        educationScore: Int,
        workScore: Int,
        languageScore: Int,
        additionalScore: Int
    ): String {
        return when {
            additionalScore >= 600 -> "PNP (Nomination Provinciale)"
            workScore >= 10 && languageScore >= 16 -> "CEC (Canadian Experience Class)"
            educationScore >= 20 && languageScore >= 16 -> "FSW (Federal Skilled Worker)"
            else -> "FST (Federal Skilled Trades)"
        }
    }

    private fun determineEligibility(
        totalScore: Int,
        languageScore: Int,
        educationScore: Int
    ): EligibilityStatus {
        return when {
            totalScore >= 450 -> EligibilityStatus.ELIGIBLE
            totalScore >= 350 && languageScore >= 16 && educationScore >= 15 -> EligibilityStatus.NEEDS_IMPROVEMENT
            else -> EligibilityStatus.NOT_ELIGIBLE
        }
    }

    /**
     * Génère un message d'éligibilité basé sur le résultat
     */
    fun getEligibilityMessage(result: CRSResult): String {
        return when (result.eligibility) {
            EligibilityStatus.ELIGIBLE -> "✅ Éligible - Score CRS: ${result.totalScore}"
            EligibilityStatus.NEEDS_IMPROVEMENT -> "⚠️ À améliorer - Score CRS: ${result.totalScore}"
            EligibilityStatus.NOT_ELIGIBLE -> "❌ Non éligible pour l'instant - Score CRS: ${result.totalScore}"
        }
    }
}
