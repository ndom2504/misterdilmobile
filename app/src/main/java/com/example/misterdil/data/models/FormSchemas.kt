package com.example.misterdil.data.models

object FormSchemas {
    
    // ==================== SECTIONS COMMUNES ====================
    
    private val personalInfoSection = FormSection(
        id = "personal_info",
        title = "Informations personnelles",
        description = "Informations de base requises pour tous les dossiers",
        order = 1,
        fields = listOf(
            FormField(
                id = "first_name",
                label = "Prénom(s)",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Votre prénom"
            ),
            FormField(
                id = "last_name",
                label = "Nom",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Votre nom de famille"
            ),
            FormField(
                id = "gender",
                label = "Sexe",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Homme", "Femme", "Autre", "Préfère ne pas dire")
            ),
            FormField(
                id = "birth_date",
                label = "Date de naissance",
                type = FieldType.DATE,
                required = true
            ),
            FormField(
                id = "birth_country",
                label = "Pays de naissance",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("France", "Maroc", "Algérie", "Tunisie", "Sénégal", "Côte d'Ivoire", "Autre")
            ),
            FormField(
                id = "nationality",
                label = "Nationalité",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Française", "Marocaine", "Algérienne", "Tunisienne", "Sénégalaise", "Ivoirienne", "Autre")
            ),
            FormField(
                id = "marital_status",
                label = "État civil",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Célibataire", "Marié(e)", "Divorcé(e)", "Veuf/Veuve", "Union libre")
            ),
            FormField(
                id = "preferred_language",
                label = "Langue préférée",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Français", "Anglais", "Espagnol", "Arabe")
            ),
            FormField(
                id = "passport_number",
                label = "Numéro de passeport",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Numéro de votre passeport"
            ),
            FormField(
                id = "passport_country",
                label = "Pays d'émission",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("France", "Maroc", "Algérie", "Tunisie", "Sénégal", "Côte d'Ivoire", "Autre")
            ),
            FormField(
                id = "passport_expiry",
                label = "Date d'expiration",
                type = FieldType.DATE,
                required = true
            )
        )
    )
    
    private val contactSection = FormSection(
        id = "contact",
        title = "Coordonnées",
        description = "Vos informations de contact",
        order = 2,
        fields = listOf(
            FormField(
                id = "address",
                label = "Adresse actuelle",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Votre adresse complète"
            ),
            FormField(
                id = "country_of_residence",
                label = "Pays de résidence",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("France", "Maroc", "Algérie", "Tunisie", "Sénégal", "Côte d'Ivoire", "Autre")
            ),
            FormField(
                id = "phone",
                label = "Téléphone",
                type = FieldType.TEXT,
                required = true,
                placeholder = "+33 6 12 34 56 78"
            ),
            FormField(
                id = "email",
                label = "Email",
                type = FieldType.TEXT,
                required = true,
                placeholder = "votre@email.com"
            )
        )
    )
    
    private val familySection = FormSection(
        id = "family",
        title = "Situation familiale",
        description = "Informations sur votre famille",
        order = 3,
        fields = listOf(
            FormField(
                id = "has_spouse",
                label = "Avez-vous un conjoint?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "spouse_name",
                label = "Nom du conjoint",
                type = FieldType.TEXT,
                required = false,
                visibleIf = ConditionalRule("has_spouse", "equals", "true"),
                placeholder = "Nom complet du conjoint"
            ),
            FormField(
                id = "spouse_birth_date",
                label = "Date de naissance du conjoint",
                type = FieldType.DATE,
                required = false,
                visibleIf = ConditionalRule("has_spouse", "equals", "true")
            ),
            FormField(
                id = "spouse_nationality",
                label = "Nationalité du conjoint",
                type = FieldType.DROPDOWN,
                required = false,
                visibleIf = ConditionalRule("has_spouse", "equals", "true"),
                options = listOf("Française", "Marocaine", "Algérienne", "Tunisienne", "Sénégalaise", "Ivoirienne", "Autre")
            ),
            FormField(
                id = "has_children",
                label = "Avez-vous des enfants?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "children_count",
                label = "Nombre d'enfants",
                type = FieldType.NUMBER,
                required = false,
                visibleIf = ConditionalRule("has_children", "equals", "true"),
                min = 0,
                max = 20
            )
        )
    )
    
    private val immigrationHistorySection = FormSection(
        id = "immigration_history",
        title = "Historique immigration Canada",
        description = "Vos expériences passées avec l'immigration canadienne",
        order = 4,
        fields = listOf(
            FormField(
                id = "applied_visa",
                label = "Avez-vous déjà demandé un visa canadien?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "visa_refused",
                label = "Avez-vous déjà été refusé?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "stayed_canada",
                label = "Avez-vous déjà séjourné au Canada?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "immigration_details",
                label = "Détails si oui",
                type = FieldType.TEXT_AREA,
                required = false,
                placeholder = "Précisez les dates, types de visa, raisons du refus, etc.",
                visibleIf = ConditionalRule("applied_visa", "equals", "true")
            )
        )
    )
    
    // ==================== ENTRÉE EXPRESS ====================
    
    private val educationSection = FormSection(
        id = "education",
        title = "Études",
        description = "Votre parcours académique",
        order = 5,
        fields = listOf(
            FormField(
                id = "education_level",
                label = "Niveau d'études le plus élevé",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Doctorat", "Master", "Licence", "Baccalauréat", "BTS/DUT", "Autre")
            ),
            FormField(
                id = "degree_obtained",
                label = "Diplôme obtenu",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: Master en Informatique"
            ),
            FormField(
                id = "degree_country",
                label = "Pays d'obtention",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("France", "Maroc", "Algérie", "Tunisie", "Sénégal", "Côte d'Ivoire", "Canada", "Autre")
            ),
            FormField(
                id = "degree_year",
                label = "Année d'obtention",
                type = FieldType.NUMBER,
                required = true,
                min = 1950,
                max = 2026
            ),
            FormField(
                id = "has_eca",
                label = "Avez-vous une ECA (Évaluation des diplômes)?",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Oui", "Non", "En cours")
            )
        )
    )
    
    private val workExperienceSection = FormSection(
        id = "work_experience",
        title = "Expérience professionnelle",
        description = "Votre expérience de travail",
        order = 6,
        fields = listOf(
            FormField(
                id = "current_job_title",
                label = "Poste actuel",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Votre titre actuel"
            ),
            FormField(
                id = "current_noc",
                label = "Code NOC (National Occupational Classification)",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: 21232",
                helperText = "Code à 4 ou 5 chiffres"
            ),
            FormField(
                id = "current_experience_years",
                label = "Années d'expérience dans ce poste",
                type = FieldType.NUMBER,
                required = true,
                min = 0,
                max = 50
            ),
            FormField(
                id = "has_canadian_experience",
                label = "Avez-vous de l'expérience de travail au Canada?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "canadian_experience_years",
                label = "Années d'expérience canadienne",
                type = FieldType.NUMBER,
                required = false,
                visibleIf = ConditionalRule("has_canadian_experience", "equals", "true"),
                min = 0,
                max = 50
            )
        )
    )
    
    private val languageSection = FormSection(
        id = "language",
        title = "Langues",
        description = "Vos compétences linguistiques",
        order = 7,
        fields = listOf(
            FormField(
                id = "main_language_test",
                label = "Test de langue principal",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("IELTS", "TEF", "CELPIP", "Autre", "Pas encore passé")
            ),
            FormField(
                id = "listening_score",
                label = "Score - Compréhension",
                type = FieldType.NUMBER,
                required = false,
                visibleIf = ConditionalRule("main_language_test", "not_equals", "Pas encore passé"),
                min = 0,
                max = 10
            ),
            FormField(
                id = "speaking_score",
                label = "Score - Expression orale",
                type = FieldType.NUMBER,
                required = false,
                visibleIf = ConditionalRule("main_language_test", "not_equals", "Pas encore passé"),
                min = 0,
                max = 10
            ),
            FormField(
                id = "reading_score",
                label = "Score - Lecture",
                type = FieldType.NUMBER,
                required = false,
                visibleIf = ConditionalRule("main_language_test", "not_equals", "Pas encore passé"),
                min = 0,
                max = 10
            ),
            FormField(
                id = "writing_score",
                label = "Score - Écriture",
                type = FieldType.NUMBER,
                required = false,
                visibleIf = ConditionalRule("main_language_test", "not_equals", "Pas encore passé"),
                min = 0,
                max = 10
            ),
            FormField(
                id = "test_date",
                label = "Date du test",
                type = FieldType.DATE,
                required = false,
                visibleIf = ConditionalRule("main_language_test", "not_equals", "Pas encore passé")
            ),
            FormField(
                id = "has_second_language",
                label = "Avez-vous une langue secondaire?",
                type = FieldType.CHECKBOX,
                required = true
            )
        )
    )
    
    private val additionalFactorsSection = FormSection(
        id = "additional_factors",
        title = "Facteurs additionnels",
        description = "Éléments bonus pour votre dossier",
        order = 8,
        fields = listOf(
            FormField(
                id = "has_job_offer",
                label = "Offre d'emploi valide?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "has_provincial_nomination",
                label = "Nomination provinciale?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "studied_in_canada",
                label = "Études au Canada?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "family_in_canada",
                label = "Famille au Canada?",
                type = FieldType.CHECKBOX,
                required = true
            )
        )
    )
    
    private val crsSummarySection = FormSection(
        id = "crs_summary",
        title = "Résumé CRS",
        description = "Votre score estimé et programme probable",
        order = 9,
        fields = listOf(
            FormField(
                id = "crs_score",
                label = "Score CRS estimé",
                type = FieldType.READ_ONLY,
                required = false,
                value = "Calcul en cours..."
            ),
            FormField(
                id = "eligible_program",
                label = "Programme probable",
                type = FieldType.READ_ONLY,
                required = false,
                value = "FSW (Federal Skilled Worker)"
            ),
            FormField(
                id = "eligibility_status",
                label = "Statut d'éligibilité",
                type = FieldType.READ_ONLY,
                required = false,
                value = "⚠️ Informations incomplètes"
            )
        )
    )
    
    // ==================== PERMIS D'ÉTUDES ====================
    
    private val studyProjectSection = FormSection(
        id = "study_project",
        title = "Projet d'études",
        description = "Détails de votre projet d'études",
        order = 5,
        fields = listOf(
            FormField(
                id = "institution_name",
                label = "Nom de l'établissement",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Université ou collège"
            ),
            FormField(
                id = "institution_country",
                label = "Pays de l'établissement",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Canada", "France", "Autre")
            ),
            FormField(
                id = "program_name",
                label = "Nom du programme",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: Master en Sciences de l'information"
            ),
            FormField(
                id = "program_level",
                label = "Niveau du programme",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Doctorat", "Master", "Licence", "Baccalauréat professionnel", "Autre")
            ),
            FormField(
                id = "program_duration",
                label = "Durée du programme",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: 2 ans"
            ),
            FormField(
                id = "admission_letter_status",
                label = "Lettre d'admission",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Reçue", "En attente", "Pas encore")
            )
        )
    )
    
    private val motivationSection = FormSection(
        id = "motivation",
        title = "Motivation & parcours",
        description = "Votre parcours et vos objectifs",
        order = 6,
        fields = listOf(
            FormField(
                id = "last_education_completed",
                label = "Dernier niveau d'études complété",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: Master en Droit"
            ),
            FormField(
                id = "studies_link",
                label = "Lien entre vos études passées et ce projet",
                type = FieldType.TEXT_AREA,
                required = true,
                placeholder = "Expliquez la cohérence de votre parcours",
                maxLength = 500
            ),
            FormField(
                id = "career_objective",
                label = "Objectif professionnel après les études",
                type = FieldType.TEXT_AREA,
                required = true,
                placeholder = "Que souhaitez-vous faire après votre diplôme?",
                maxLength = 500
            ),
            FormField(
                id = "return_intention",
                label = "Intention de retour",
                type = FieldType.TEXT_AREA,
                required = true,
                placeholder = "Expliquez vos intentions après vos études",
                helperText = "Ce champ est important pour démontrer votre intention de retour",
                maxLength = 500
            )
        )
    )
    
    private val financialSection = FormSection(
        id = "financial",
        title = "Situation financière",
        description = "Vos ressources financières",
        order = 7,
        fields = listOf(
            FormField(
                id = "funding_source",
                label = "Qui finance vos études?",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Moi-même", "Parents", "Tierce personne", "Bourse", "Mixte")
            ),
            FormField(
                id = "available_amount",
                label = "Montant disponible (CAD)",
                type = FieldType.NUMBER,
                required = true,
                placeholder = "Montant en dollars canadiens",
                helperText = "Frais de scolarité + frais de vie pour 1 an"
            ),
            FormField(
                id = "financial_proofs",
                label = "Preuves financières disponibles",
                type = FieldType.MULTI_SELECT,
                required = true,
                options = listOf("Relevés bancaires", "Lettre de bourse", "Garantie financière", "Autre")
            )
        )
    )
    
    private val travelHistorySection = FormSection(
        id = "travel_history",
        title = "Historique voyages & visas",
        description = "Vos expériences de voyage",
        order = 8,
        fields = listOf(
            FormField(
                id = "recent_travel",
                label = "Voyages récents (pays visités)",
                type = FieldType.TEXT_AREA,
                required = false,
                placeholder = "Liste des pays visités ces 5 dernières années",
                maxLength = 300
            ),
            FormField(
                id = "visa_refusals",
                label = "Refus de visa (tous pays)",
                type = FieldType.TEXT_AREA,
                required = true,
                placeholder = "Précisez si vous avez eu des refus",
                maxLength = 300
            ),
            FormField(
                id = "compliance_history",
                label = "Respect des conditions de séjour passées",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Toujours respecté", "Infraction mineure", "Infraction majeure", "Non applicable")
            )
        )
    )
    
    private val requiredDocumentsSection = FormSection(
        id = "required_documents",
        title = "Documents requis",
        description = "Documents à fournir",
        order = 9,
        fields = listOf(
            FormField(
                id = "has_admission_letter",
                label = "Lettre d'admission",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "has_financial_proofs",
                label = "Preuves financières",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "has_passport",
                label = "Passeport valide",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "has_diplomas",
                label = "Diplômes et relevés de notes",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "has_explanation_letter",
                label = "Lettre explicative (optionnel)",
                type = FieldType.CHECKBOX,
                required = false
            )
        )
    )
    
    // ==================== SCHÉMAS COMPLETS ====================
    
    val entreeExpressSchema = FormSchema(
        id = "entree_express",
        title = "Entrée Express",
        description = "Formulaire pour évaluer votre éligibilité à l'Entrée Express",
        dossierType = "ENTREE_EXPRESS",
        sections = listOf(
            personalInfoSection,
            contactSection,
            familySection,
            immigrationHistorySection,
            educationSection,
            workExperienceSection,
            languageSection,
            additionalFactorsSection,
            crsSummarySection
        )
    )
    
    val permisEtudesSchema = FormSchema(
        id = "permis_etudes",
        title = "Permis d'études",
        description = "Formulaire pour votre demande de permis d'études",
        dossierType = "PERMIS_ETUDES",
        sections = listOf(
            personalInfoSection,
            contactSection,
            familySection,
            immigrationHistorySection,
            studyProjectSection,
            motivationSection,
            financialSection,
            travelHistorySection,
            requiredDocumentsSection
        )
    )
    
    fun getSchemaByDossierType(dossierType: String): FormSchema? {
        return when (dossierType.uppercase()) {
            "ENTREE_EXPRESS" -> entreeExpressSchema
            "PERMIS_ETUDES" -> permisEtudesSchema
            else -> null
        }
    }
}
