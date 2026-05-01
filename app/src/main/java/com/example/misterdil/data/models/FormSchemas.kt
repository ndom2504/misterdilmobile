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
                options = listOf(
                    "Afghanistan", "Afrique du Sud", "Albanie", "Algérie", "Allemagne", "Andorre", "Angola", "Antigua-et-Barbuda", "Arabie saoudite", "Argentine", "Arménie", "Australie", "Autriche", "Azerbaïdjan",
                    "Bahamas", "Bahreïn", "Bangladesh", "Barbade", "Belgique", "Belize", "Bénin", "Bhoutan", "Biélorussie", "Bolivie", "Bosnie-Herzégovine", "Botswana", "Brésil", "Brunei", "Bulgarie", "Burkina Faso", "Burundi",
                    "Cambodge", "Cameroun", "Canada", "Cap-Vert", "Chili", "Chine", "Chypre", "Colombie", "Comores", "Congo", "Corée du Nord", "Corée du Sud", "Costa Rica", "Côte d'Ivoire", "Croatie", "Cuba",
                    "Danemark", "Djibouti", "Dominique",
                    "Égypte", "Émirats arabes unis", "Équateur", "Érythrée", "Espagne", "Estonie", "États-Unis", "Éthiopie",
                    "Fidji", "Finlande", "France",
                    "Gabon", "Gambie", "Géorgie", "Ghana", "Grèce", "Grenade", "Guatemala", "Guinée", "Guinée équatoriale", "Guinée-Bissau", "Guyane",
                    "Haïti", "Honduras", "Hongrie",
                    "Inde", "Indonésie", "Irak", "Iran", "Irlande", "Islande", "Israël", "Italie",
                    "Jamaïque", "Japon", "Jordanie",
                    "Kazakhstan", "Kenya", "Kiribati", "Koweït", "Kyrgyzstan",
                    "Laos", "Lesotho", "Lettonie", "Liban", "Liberia", "Libye", "Liechtenstein", "Lituanie", "Luxembourg",
                    "Macédoine", "Madagascar", "Malaisie", "Malawi", "Maldives", "Mali", "Malte", "Maroc", "Maurice", "Mauritanie", "Mexique", "Micronésie", "Moldavie", "Monaco", "Mongolie", "Monténégro", "Mozambique", "Myanmar",
                    "Namibie", "Nauru", "Népal", "Nicaragua", "Niger", "Nigeria", "Norvège", "Nouvelle-Zélande",
                    "Oman", "Ouganda", "Ouzbékistan",
                    "Pakistan", "Panama", "Papouasie-Nouvelle-Guinée", "Paraguay", "Pays-Bas", "Pérou", "Philippines", "Pologne", "Portugal", "Qatar",
                    "République centrafricaine", "République démocratique du Congo", "République dominicaine", "Roumanie", "Royaume-Uni", "Russie", "Rwanda",
                    "Saint-Christophe-et-Niévès", "Sainte-Lucie", "Saint-Marin", "Saint-Vincent-et-les-Grenadines", "Salomon", "Salvador", "Samoa", "Sao Tomé-et-Principe", "Sénégal", "Serbie", "Seychelles", "Sierra Leone", "Singapour", "Slovaquie", "Slovénie", "Somalie", "Soudan", "Sri Lanka", "Suède", "Suisse", "Suriname", "Swaziland", "Syrie",
                    "Tadjikistan", "Tanzanie", "Tchad", "Thaïlande", "Togo", "Tonga", "Trinité-et-Tobago", "Tunisie", "Turkménistan", "Turquie", "Tuvalu",
                    "Ukraine", "Uruguay",
                    "Vanuatu", "Vatican", "Venezuela", "Vietnam",
                    "Yémen",
                    "Zambie", "Zimbabwe"
                )
            ),
            FormField(
                id = "nationality",
                label = "Nationalité",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf(
                    "Afghan", "Afriquain du Sud", "Albanais", "Algérien", "Allemand", "Andorran", "Angolais", "Antiguais", "Arabe saoudite", "Argentin", "Arménien", "Australien", "Autrichien", "Azerbaïdjanais",
                    "Bahamien", "Bahreïni", "Bangladais", "Barbadien", "Belge", "Bélizien", "Béninois", "Bhoutanais", "Biélorusse", "Bolivien", "Bosnien", "Botswanais", "Brésilien", "Brunéien", "Bulgare", "Burkinabé", "Burundais",
                    "Cambodgien", "Camerounais", "Canadien", "Cap-Verdien", "Chilien", "Chinois", "Chypriote", "Colombien", "Comorien", "Congolais", "Coréen du Nord", "Coréen du Sud", "Costaricien", "Ivoirien", "Croate", "Cubain",
                    "Danois", "Djiboutien", "Dominicain",
                    "Égyptien", "Émirati", "Équatorien", "Érythréen", "Espagnol", "Estonien", "Américain", "Éthiopien",
                    "Fidjien", "Finlandais", "Français",
                    "Gabonais", "Gambien", "Géorgien", "Ghanéen", "Grec", "Grenadien", "Guatémaltèque", "Guinéen", "Guinéen équatorial", "Bissau-guinéen", "Guyanien",
                    "Haïtien", "Hondurien", "Hongrois",
                    "Indien", "Indonésien", "Irakien", "Iranien", "Irlandais", "Islandais", "Israélien", "Italien",
                    "Jamaïcain", "Japonais", "Jordanien",
                    "Kazakh", "Kényan", "Kiribatien", "Koweïtien", "Kirghize",
                    "Laotien", "Lesothan", "Letton", "Libanais", "Libérien", "Libyen", "Liechtensteinois", "Lituanien", "Luxembourgeois",
                    "Macédonien", "Malgache", "Malaisien", "Malawite", "Maldivien", "Malien", "Maltais", "Marocain", "Mauricien", "Mauritanien", "Mexicain", "Micronésien", "Moldave", "Monégasque", "Mongol", "Monténégrin", "Mozambicain", "Birman",
                    "Namibien", "Nauruan", "Népalais", "Nicaraguayen", "Nigérien", "Nigérian", "Norvégien", "Néo-Zélandais",
                    "Omanais", "Ougandais", "Ouzbékistanais",
                    "Pakistanais", "Panaméen", "Papouasien", "Paraguayen", "Néerlandais", "Péruvien", "Philippin", "Polonais", "Portugais", "Qatari",
                    "Centrafricain", "Congolais (RDC)", "Dominicain (République)", "Roumain", "Britannique", "Russe", "Rwandais",
                    "Saint-Kittsien", "Saint-Lucien", "Saint-Marinais", "Saint-Vincentais", "Salomonais", "Salvadorien", "Samoan", "Santoméen", "Sénégalais", "Serbe", "Seychellois", "Sierra-Léonais", "Singapourien", "Slovaque", "Slovène", "Somalien", "Soudanais", "Sri-lankais", "Suédois", "Suisse", "Surinamais", "Swazi", "Syrien",
                    "Tadjik", "Tanzanien", "Tchadien", "Thaïlandais", "Togolais", "Tonguien", "Trinidadien", "Tunisien", "Turkmène", "Turc", "Tuvaluan",
                    "Ukrainien", "Uruguayen",
                    "Vanuatais", "Vaticanais", "Vénézuélien", "Vietnamien",
                    "Yéménite",
                    "Zambien", "Zimbabwéen"
                )
            ),
            FormField(
                id = "country_of_residence",
                label = "Pays de résidence",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf(
                    "Afghanistan", "Afrique du Sud", "Albanie", "Algérie", "Allemagne", "Andorre", "Angola", "Antigua-et-Barbuda", "Arabie saoudite", "Argentine", "Arménie", "Australie", "Autriche", "Azerbaïdjan",
                    "Bahamas", "Bahreïn", "Bangladesh", "Barbade", "Belgique", "Belize", "Bénin", "Bhoutan", "Biélorussie", "Bolivie", "Bosnie-Herzégovine", "Botswana", "Brésil", "Brunei", "Bulgarie", "Burkina Faso", "Burundi",
                    "Cambodge", "Cameroun", "Canada", "Cap-Vert", "Chili", "Chine", "Chypre", "Colombie", "Comores", "Congo", "Corée du Nord", "Corée du Sud", "Costa Rica", "Côte d'Ivoire", "Croatie", "Cuba",
                    "Danemark", "Djibouti", "Dominique",
                    "Égypte", "Émirats arabes unis", "Équateur", "Érythrée", "Espagne", "Estonie", "États-Unis", "Éthiopie",
                    "Fidji", "Finlande", "France",
                    "Gabon", "Gambie", "Géorgie", "Ghana", "Grèce", "Grenade", "Guatemala", "Guinée", "Guinée équatoriale", "Guinée-Bissau", "Guyane",
                    "Haïti", "Honduras", "Hongrie",
                    "Inde", "Indonésie", "Irak", "Iran", "Irlande", "Islande", "Israël", "Italie",
                    "Jamaïque", "Japon", "Jordanie",
                    "Kazakhstan", "Kenya", "Kiribati", "Koweït", "Kyrgyzstan",
                    "Laos", "Lesotho", "Lettonie", "Liban", "Liberia", "Libye", "Liechtenstein", "Lituanie", "Luxembourg",
                    "Macédoine", "Madagascar", "Malaisie", "Malawi", "Maldives", "Mali", "Malte", "Maroc", "Maurice", "Mauritanie", "Mexique", "Micronésie", "Moldavie", "Monaco", "Mongolie", "Monténégro", "Mozambique", "Myanmar",
                    "Namibie", "Nauru", "Népal", "Nicaragua", "Niger", "Nigeria", "Norvège", "Nouvelle-Zélande",
                    "Oman", "Ouganda", "Ouzbékistan",
                    "Pakistan", "Panama", "Papouasie-Nouvelle-Guinée", "Paraguay", "Pays-Bas", "Pérou", "Philippines", "Pologne", "Portugal", "Qatar",
                    "République centrafricaine", "République démocratique du Congo", "République dominicaine", "Roumanie", "Royaume-Uni", "Russie", "Rwanda",
                    "Saint-Christophe-et-Niévès", "Sainte-Lucie", "Saint-Marin", "Saint-Vincent-et-les-Grenadines", "Salomon", "Salvador", "Samoa", "Sao Tomé-et-Principe", "Sénégal", "Serbie", "Seychelles", "Sierra Leone", "Singapour", "Slovaquie", "Slovénie", "Somalie", "Soudan", "Sri Lanka", "Suède", "Suisse", "Suriname", "Swaziland", "Syrie",
                    "Tadjikistan", "Tanzanie", "Tchad", "Thaïlande", "Togo", "Tonga", "Trinité-et-Tobago", "Tunisie", "Turkménistan", "Turquie", "Tuvalu",
                    "Ukraine", "Uruguay",
                    "Vanuatu", "Vatican", "Venezuela", "Vietnam",
                    "Yémen",
                    "Zambie", "Zimbabwe"
                )
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
                options = listOf(
                    "Afghanistan", "Afrique du Sud", "Albanie", "Algérie", "Allemagne", "Andorre", "Angola", "Antigua-et-Barbuda", "Arabie saoudite", "Argentine", "Arménie", "Australie", "Autriche", "Azerbaïdjan",
                    "Bahamas", "Bahreïn", "Bangladesh", "Barbade", "Belgique", "Belize", "Bénin", "Bhoutan", "Biélorussie", "Bolivie", "Bosnie-Herzégovine", "Botswana", "Brésil", "Brunei", "Bulgarie", "Burkina Faso", "Burundi",
                    "Cambodge", "Cameroun", "Canada", "Cap-Vert", "Chili", "Chine", "Chypre", "Colombie", "Comores", "Congo", "Corée du Nord", "Corée du Sud", "Costa Rica", "Côte d'Ivoire", "Croatie", "Cuba",
                    "Danemark", "Djibouti", "Dominique",
                    "Égypte", "Émirats arabes unis", "Équateur", "Érythrée", "Espagne", "Estonie", "États-Unis", "Éthiopie",
                    "Fidji", "Finlande", "France",
                    "Gabon", "Gambie", "Géorgie", "Ghana", "Grèce", "Grenade", "Guatemala", "Guinée", "Guinée équatoriale", "Guinée-Bissau", "Guyane",
                    "Haïti", "Honduras", "Hongrie",
                    "Inde", "Indonésie", "Irak", "Iran", "Irlande", "Islande", "Israël", "Italie",
                    "Jamaïque", "Japon", "Jordanie",
                    "Kazakhstan", "Kenya", "Kiribati", "Koweït", "Kyrgyzstan",
                    "Laos", "Lesotho", "Lettonie", "Liban", "Liberia", "Libye", "Liechtenstein", "Lituanie", "Luxembourg",
                    "Macédoine", "Madagascar", "Malaisie", "Malawi", "Maldives", "Mali", "Malte", "Maroc", "Maurice", "Mauritanie", "Mexique", "Micronésie", "Moldavie", "Monaco", "Mongolie", "Monténégro", "Mozambique", "Myanmar",
                    "Namibie", "Nauru", "Népal", "Nicaragua", "Niger", "Nigeria", "Norvège", "Nouvelle-Zélande",
                    "Oman", "Ouganda", "Ouzbékistan",
                    "Pakistan", "Panama", "Papouasie-Nouvelle-Guinée", "Paraguay", "Pays-Bas", "Pérou", "Philippines", "Pologne", "Portugal", "Qatar",
                    "République centrafricaine", "République démocratique du Congo", "République dominicaine", "Roumanie", "Royaume-Uni", "Russie", "Rwanda",
                    "Saint-Christophe-et-Niévès", "Sainte-Lucie", "Saint-Marin", "Saint-Vincent-et-les-Grenadines", "Salomon", "Salvador", "Samoa", "Sao Tomé-et-Principe", "Sénégal", "Serbie", "Seychelles", "Sierra Leone", "Singapour", "Slovaquie", "Slovénie", "Somalie", "Soudan", "Sri Lanka", "Suède", "Suisse", "Suriname", "Swaziland", "Syrie",
                    "Tadjikistan", "Tanzanie", "Tchad", "Thaïlande", "Togo", "Tonga", "Trinité-et-Tobago", "Tunisie", "Turkménistan", "Turquie", "Tuvalu",
                    "Ukraine", "Uruguay",
                    "Vanuatu", "Vatican", "Venezuela", "Vietnam",
                    "Yémen",
                    "Zambie", "Zimbabwe"
                )
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
                value = "En cours de calcul..."
            ),
            FormField(
                id = "eligible_program",
                label = "Programme probable",
                type = FieldType.READ_ONLY,
                required = false,
                value = "À déterminer"
            ),
            FormField(
                id = "eligibility_status",
                label = "Statut d'éligibilité",
                type = FieldType.READ_ONLY,
                required = false,
                value = "⏳ Remplissez le formulaire pour voir votre score"
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

    // ==================== PLAN D'AFFAIRES ====================

    private val entrepreneurialProfileSection = FormSection(
        id = "entrepreneurial_profile",
        title = "Profil entrepreneurial",
        description = "Votre expérience et compétences entrepreneuriales",
        order = 5,
        fields = listOf(
            FormField(
                id = "entrepreneurial_years",
                label = "Années d'expérience entrepreneuriale",
                type = FieldType.NUMBER,
                required = true,
                min = 0,
                max = 50
            ),
            FormField(
                id = "business_sectors",
                label = "Secteur(s) d'expérience",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: Restauration, Technologie, Commerce..."
            ),
            FormField(
                id = "previous_companies",
                label = "Avez-vous créé des entreprises précédemment?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "previous_company_name",
                label = "Nom de l'entreprise précédente",
                type = FieldType.TEXT,
                required = false,
                visibleIf = ConditionalRule("previous_companies", "equals", "true"),
                placeholder = "Nom de l'entreprise"
            ),
            FormField(
                id = "previous_company_country",
                label = "Pays de l'entreprise",
                type = FieldType.DROPDOWN,
                required = false,
                visibleIf = ConditionalRule("previous_companies", "equals", "true"),
                options = listOf("France", "Maroc", "Algérie", "Tunisie", "Sénégal", "Côte d'Ivoire", "Canada", "Autre")
            ),
            FormField(
                id = "previous_company_activity",
                label = "Activité de l'entreprise",
                type = FieldType.TEXT,
                required = false,
                visibleIf = ConditionalRule("previous_companies", "equals", "true"),
                placeholder = "Description de l'activité"
            ),
            FormField(
                id = "previous_company_result",
                label = "Résultat de l'entreprise",
                type = FieldType.DROPDOWN,
                required = false,
                visibleIf = ConditionalRule("previous_companies", "equals", "true"),
                options = listOf("Active", "Fermée", "Vendue")
            ),
            FormField(
                id = "current_position",
                label = "Poste actuel ou dernier poste",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Votre titre actuel"
            ),
            FormField(
                id = "key_skills",
                label = "Compétences clés",
                type = FieldType.TEXT_AREA,
                required = true,
                placeholder = "Vos principales compétences (3-5 points clés)",
                maxLength = 500
            )
        )
    )

    private val projectDescriptionSection = FormSection(
        id = "project_description",
        title = "Description du projet d'affaires",
        description = "Présentation de votre projet",
        order = 6,
        fields = listOf(
            FormField(
                id = "project_type",
                label = "Type de projet",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Création", "Reprise", "Franchise")
            ),
            FormField(
                id = "business_sector",
                label = "Secteur d'activité",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: Restauration, Commerce de détail, Services..."
            ),
            FormField(
                id = "planned_location",
                label = "Localisation prévue",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Province / Ville souhaitée"
            ),
            FormField(
                id = "project_description",
                label = "Description du projet",
                type = FieldType.TEXT_AREA,
                required = true,
                placeholder = "Décrivez votre projet en 3-5 lignes",
                maxLength = 500
            ),
            FormField(
                id = "project_status",
                label = "Statut actuel du projet",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Idée", "En cours", "Entreprise existante")
            )
        )
    )

    private val marketSection = FormSection(
        id = "market",
        title = "Marché & Clientèle",
        description = "Analyse de votre marché cible",
        order = 7,
        fields = listOf(
            FormField(
                id = "target_clients",
                label = "Clients ciblés",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Particuliers", "Entreprises", "Les deux")
            ),
            FormField(
                id = "problem_solved",
                label = "Problème résolu par votre projet",
                type = FieldType.TEXT_AREA,
                required = true,
                placeholder = "Quel besoin répondez-vous? (3-5 lignes)",
                maxLength = 500
            ),
            FormField(
                id = "has_competitors",
                label = "Avez-vous identifié des concurrents?",
                type = FieldType.CHECKBOX,
                required = true
            ),
            FormField(
                id = "competitors_details",
                label = "Quels sont vos concurrents?",
                type = FieldType.TEXT_AREA,
                required = false,
                visibleIf = ConditionalRule("has_competitors", "equals", "true"),
                placeholder = "Listez vos principaux concurrents",
                maxLength = 300
            ),
            FormField(
                id = "competitive_advantage",
                label = "Avantage concurrentiel principal",
                type = FieldType.TEXT_AREA,
                required = true,
                placeholder = "Qu'est-ce qui vous différencie? (3-5 lignes)",
                maxLength = 500
            )
        )
    )

    private val economicModelSection = FormSection(
        id = "economic_model",
        title = "Modèle économique",
        description = "Comment votre entreprise génère des revenus",
        order = 8,
        fields = listOf(
            FormField(
                id = "revenue_source",
                label = "Source de revenus principale",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: Vente de produits, Services, Abonnements..."
            ),
            FormField(
                id = "average_price",
                label = "Prix moyen d'un produit/service (CAD)",
                type = FieldType.NUMBER,
                required = true,
                placeholder = "Montant en dollars canadiens"
            ),
            FormField(
                id = "sales_frequency",
                label = "Fréquence des ventes",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Quotidienne", "Hebdomadaire", "Mensuelle", "Saisonnière", "Autre")
            ),
            FormField(
                id = "main_expenses",
                label = "Charges principales",
                type = FieldType.MULTI_SELECT,
                required = true,
                options = listOf("Loyer", "Salaires", "Fournisseurs", "Marketing", "Équipement", "Autre")
            ),
            FormField(
                id = "break_even",
                label = "Seuil de rentabilité estimé",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("< 1 an", "1-2 ans", "> 2 ans")
            )
        )
    )

    private val investmentSection = FormSection(
        id = "investment",
        title = "Investissement & Financement",
        description = "Plan de financement de votre projet",
        order = 9,
        fields = listOf(
            FormField(
                id = "total_investment",
                label = "Investissement total prévu (CAD)",
                type = FieldType.NUMBER,
                required = true,
                placeholder = "Montant total en dollars canadiens"
            ),
            FormField(
                id = "available_funds",
                label = "Fonds propres disponibles (CAD)",
                type = FieldType.NUMBER,
                required = true,
                placeholder = "Montant en dollars canadiens"
            ),
            FormField(
                id = "funds_source",
                label = "Source des fonds",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Personnel", "Partenaires", "Prêt", "Combinaison")
            ),
            FormField(
                id = "personal_contribution",
                label = "Apport personnel (%)",
                type = FieldType.NUMBER,
                required = true,
                min = 0,
                max = 100,
                placeholder = "Pourcentage de votre apport"
            ),
            FormField(
                id = "has_proofs",
                label = "Avez-vous des preuves financières disponibles?",
                type = FieldType.CHECKBOX,
                required = true
            )
        )
    )

    private val jobsSection = FormSection(
        id = "jobs",
        title = "Emplois & Impact économique",
        description = "Impact économique de votre projet",
        order = 10,
        fields = listOf(
            FormField(
                id = "jobs_created",
                label = "Nombre d'emplois créés",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("0", "1-2", "3-5", "6-10", "10+")
            ),
            FormField(
                id = "job_types",
                label = "Type d'emplois",
                type = FieldType.TEXT,
                required = true,
                placeholder = "Ex: Commercial, Technique, Administratif..."
            ),
            FormField(
                id = "local_impact",
                label = "Impact local estimé",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("Économique", "Social", "Les deux")
            ),
            FormField(
                id = "local_partnerships",
                label = "Partenariats locaux envisagés",
                type = FieldType.CHECKBOX,
                required = true
            )
        )
    )

    private val timelineSection = FormSection(
        id = "timeline",
        title = "Calendrier du projet",
        description = "Planning de mise en œuvre",
        order = 11,
        fields = listOf(
            FormField(
                id = "start_date",
                label = "Date de démarrage prévue",
                type = FieldType.DATE,
                required = true
            ),
            FormField(
                id = "key_steps",
                label = "Étapes clés",
                type = FieldType.MULTI_SELECT,
                required = true,
                options = listOf("Installation", "Lancement", "Première vente", "Expansion")
            ),
            FormField(
                id = "development_horizon",
                label = "Horizon de développement",
                type = FieldType.DROPDOWN,
                required = true,
                options = listOf("1 an", "3 ans", "5 ans")
            )
        )
    )

    private val businessDocumentsSection = FormSection(
        id = "business_documents",
        title = "Documents (optionnel)",
        description = "Documents disponibles pour votre projet",
        order = 12,
        fields = listOf(
            FormField(
                id = "has_cv",
                label = "CV disponible",
                type = FieldType.CHECKBOX,
                required = false
            ),
            FormField(
                id = "has_financial_proofs",
                label = "Preuves financières disponibles",
                type = FieldType.CHECKBOX,
                required = false
            ),
            FormField(
                id = "has_business_plan",
                label = "Business plan existant",
                type = FieldType.CHECKBOX,
                required = false
            ),
            FormField(
                id = "has_partnership_letters",
                label = "Lettres d'intention/partenaires",
                type = FieldType.CHECKBOX,
                required = false
            )
        )
    )

    private val businessSummarySection = FormSection(
        id = "business_summary",
        title = "Résumé du projet",
        description = "Synthèse de votre projet d'affaires",
        order = 13,
        fields = listOf(
            FormField(
                id = "summary_type",
                label = "Type de projet",
                type = FieldType.READ_ONLY,
                required = false,
                value = "En cours d'évaluation"
            ),
            FormField(
                id = "summary_investment",
                label = "Montant d'investissement",
                type = FieldType.READ_ONLY,
                required = false,
                value = "À déterminer"
            ),
            FormField(
                id = "summary_jobs",
                label = "Emplois prévus",
                type = FieldType.READ_ONLY,
                required = false,
                value = "À déterminer"
            ),
            FormField(
                id = "summary_maturity",
                label = "Niveau de maturité",
                type = FieldType.READ_ONLY,
                required = false,
                value = "En cours d'évaluation"
            )
        )
    )

    val businessPlanSchema = FormSchema(
        id = "business_plan",
        title = "Plan d'affaires",
        description = "Formulaire pour évaluer votre projet d'immigration entrepreneur / investisseur",
        dossierType = "BUSINESS_PLAN",
        sections = listOf(
            personalInfoSection,
            contactSection,
            familySection,
            immigrationHistorySection,
            entrepreneurialProfileSection,
            projectDescriptionSection,
            marketSection,
            economicModelSection,
            investmentSection,
            jobsSection,
            timelineSection,
            businessDocumentsSection,
            businessSummarySection
        )
    )
    
    fun getSchemaByDossierType(dossierType: String): FormSchema? {
        return when (dossierType) {
            "Entrée Express" -> entreeExpressSchema
            "Permis d'études" -> permisEtudesSchema
            "Plan d'affaires" -> businessPlanSchema
            else -> null
        }
    }
}
