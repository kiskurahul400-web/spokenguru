import json
import re

# Vocabulary mapping for Lesson 1 (Demonstratives: This, That, These, Those)
gloss_map = {
    "who": "कौन",
    "is": "है",
    "are": "हैं",
    "was": "था / थी",
    "were": "थे / थीं",
    "this": "यह / ये",
    "that": "वह / वो",
    "these": "ये / ये सब",
    "those": "वे / वो",
    "girl": "लड़की",
    "girls": "लड़कियाँ",
    "boy": "लड़का",
    "boys": "लड़के",
    "naughty": "शैतान",
    "door": "दरवाजा",
    "locked": "बंद",
    "garden": "बगीचा",
    "looks": "लगता है",
    "lovely": "प्यारा",
    "amit": "अमित",
    "rohan": "रोहन",
    "sameer": "समीर",
    "john": "जॉन",
    "have": "पास / चुके",
    "has": "पास / चुका",
    "come": "आए",
    "from": "से",
    "the": "-",
    "village": "गाँव",
    "aren't": "नहीं हैं",
    "real": "असली",
    "cats": "बिल्लियाँ",
    "what": "क्या",
    "happened": "हुआ",
    "to": "को / तक",
    "car": "कार",
    "do": "क्या / करना",
    "you": "तुम / आप",
    "know": "जानते",
    "pots": "गमले",
    "red": "लाल",
    "in": "में",
    "colour": "रंग",
    "color": "रंग",
    "why": "क्यों",
    "kids": "बच्चे",
    "laughing": "हँस रहे",
    "where": "कहाँ",
    "did": "किया / था",
    "buy": "खरीदी",
    "watch": "घड़ी",
    "vegetable": "सब्ज़ी",
    "very": "बहुत",
    "tasty": "स्वादिष्ट",
    "i": "मैं / मुझे",
    "paint": "पेंट",
    "window": "खिड़की",
    "will": "गा / गे / गी",
    "take": "लेना / रखना",
    "care": "ध्यान",
    "of": "का / की / के",
    "books": "किताबें",
    "my": "मेरा / मेरी",
    "favourite": "पसंदीदा",
    "favorite": "पसंदीदा",
    "place": "जगह",
    "mobile": "मोबाइल",
    "chocolates": "चॉकलेट",
    "for": "के लिए",
    "me": "मुझे / मेरे",
    "dreams": "सपने",
    "worst": "सबसे बुरे",
    "come,": "आइए",
    "let's": "चलो",
    "talk": "बात",
    "about": "बारे में",
    "it": "यह / इसे",
    "party": "पार्टी",
    "boring": "बोरिंग",
    "city": "शहर",
    "small": "छोटा",
    "looking": "दिख रही",
    "clever": "चालाक",
    "friend": "दोस्त",
    "eating": "खा रहे",
    "food": "खाना",
    "our": "हमारा",
    "home": "घर",
    "black": "काले",
    "ones": "वाले",
    "shoes": "जूते",
    "dance": "डान्स",
    "performance": "परफॉरमेन्स",
    "amazing": "कमाल",
    "people": "लोग",
    "tomorrow": "कल",
    "flowers": "फूल",
    "dead": "मुरझा गये",
    "rain": "बारिश",
    "making": "कर रही",
    "feel": "महसूस",
    "sad": "उदास",
    "houses": "घर",
    "all": "सब / सभी",
    "new": "नये",
    "traffic": "ट्रैफिक",
    "gets": "होता है",
    "worse": "और बदतर",
    "every": "हर",
    "year": "साल",
    "whose": "किसका / किसकी",
    "keys": "चाबियाँ",
    "on": "पर",
    "table": "मेज़",
    "remember": "याद",
    "day": "दिन",
    "love": "पसंद / प्यार",
    "pug": "पग",
    "dogs": "कुत्ते",
    "boxes": "डब्बे",
    "up": "ऊपर",
    "tree": "पेड़",
    "beautiful": "सुन्दर",
    "apples": "सेब",
    "sweetest": "सबसे मीठे",
    "letter": "पत्र / लेटर",
    "a": "एक",
    "milk": "दूध",
    "shop": "दुकान",
    "parents": "माता-पिता",
    "laptop": "लैपटॉप",
    "musician": "संगीतकार",
    "talented": "प्रतिभाशाली",
    "hasn't": "नहीं रहा",
    "been": "रहा",
    "good": "अच्छा",
    "meetings": "मीटिंग्स",
    "held": "आयोजित",
    "want": "चाहिए",
    "necklace": "हार",
    "diwali": "दिवाली",
    "not": "नहीं",
    "brother": "भाई",
    "yours": "तुम्हारा",
    "guests": "मेहमान",
    "key": "चाबी",
    "mine": "मेरा",
    "house": "घर",
    "ours": "हमारा",
    "now": "अब",
    "man": "आदमी",
    "far": "दूर",
    "away": "दूर",
    "children": "बच्चे",
    "book": "किताब",
    "smart": "होशियार",
    "green": "हरा",
    "so": "बहुत",
    "expensive": "महँगा",
    "go": "जाते",
    "college": "कॉलेज",
    "always": "हमेशा",
    "here": "यहाँ",
    "friends": "दोस्त",
    "study": "पढ़ते",
    "at": "पर",
    "phone": "फ़ोन",
    "and": "और",
    "shelf": "शेल्फ़",
    "there": "वहाँ",
    "folder": "फ़ोल्डर",
    "put": "रख दो",
    "desk": "डेस्क",
    "over": "वहाँ",
    "building": "इमारत",
    "newyork": "न्यूयॉर्क",
    "lovely": "प्यारी",
    "evening": "शाम",
    "today": "आज",
    "bottle": "बोतल",
    "empty": "खाली",
    "but": "लेकिन",
    "grapes": "अंगूर",
    "really": "वाकई",
    "sweet!": "मीठे",
    "sweet": "मीठे",
    "difficult": "कठिन",
    "test": "परीक्षा",
    "we": "हमने",
    "had": "दी थी",
    "last": "पिछले",
    "week": "सप्ताह",
    "great,": "अच्छी",
    "great": "अच्छी",
    "isn't": "है ना",
    "their": "अपना",
    "lunch": "दोपहर का खाना",
    "which": "कौन सा",
    "would": "चाहेंगे",
    "like": "पसंद",
    "jacket": "जैकेट",
    "most": "सबसे",
    "when": "कब",
    "going": "जा रहे",
    "sent": "भेजे",
    "t-shirt": "टी-शर्ट",
    "music": "संगीत",
    "makes": "करता है",
    "happy": "खुश",
    "performances": "प्रदर्शन",
    "train": "ट्रेन",
    "mathura": "मथुरा",
    "via": "होते हुए",
    "dehradun": "देहरादून",
    "final": "अंतिम",
    "step": "चरण",
    "might": "हो सकता",
    "be": "होना",
    "toughest": "सबसे कठिन",
    "decision": "निर्णय",
    "far": "तक",
    "time": "समय",
    "thing": "चीज़",
    "does": "करता",
    "bother": "परेशान",
    "eyes": "आँखें",
    "mocking": "मज़ाक उड़ा",
    "her": "उसका",
    "kitchen": "रसोई",
    "ideas": "विचार",
    "stolen": "चुराए",
    "sitting": "बैठे",
    "became": "हो गए",
    "silent": "चुप",
    "kept": "रहे",
    "watching": "देखते",
    "probably": "शायद",
    "his": "उसके",
    "left": "चले गए",
    "photos": "तस्वीरें",
    "much": "काफी",
    "better": "बेहतर",
    "than": "से",
    "grandparents": "दादा-दादी",
    "don't": "नहीं",
    "scissors": "कैंचियाँ",
    "cut": "काटती",
    "well": "अच्छे से"
}

def get_word_gloss(w):
    clean = re.sub(r"[^\w']", "", w).lower()
    return gloss_map.get(clean, clean)

def get_grammar_rule(hindi, ans):
    ans_lower = ans.lower()
    if "those" in ans_lower:
        return (
            "Use of THOSE (दूर स्थित बहुवचन)",
            "\"Those\" का प्रयोग दूर स्थित बहुवचन (Plural) वस्तुओं या व्यक्तियों के लिए किया जाता है।\nSingular = That (वह) • Plural = Those (वे / वो)"
        )
    elif "these" in ans_lower:
        return (
            "Use of THESE (पास स्थित बहुवचन)",
            "\"These\" का प्रयोग पास स्थित बहुवचन (Plural) वस्तुओं या व्यक्तियों के लिए किया जाता है।\nSingular = This (यह) • Plural = These (ये / ये सब)"
        )
    elif "that" in ans_lower:
        return (
            "Use of THAT (दूर स्थित एकवचन)",
            "\"That\" का प्रयोग दूर स्थित एकवचन (Singular) वस्तु या व्यक्ति के लिए किया जाता है।\nThat = वह / वो (Far Singular)"
        )
    elif "this" in ans_lower:
        return (
            "Use of THIS (पास स्थित एकवचन)",
            "\"This\" का प्रयोग पास स्थित एकवचन (Singular) वस्तु या व्यक्ति के लिए किया जाता है।\nThis = यह / ये (Near Singular)"
        )
    else:
        return (
            "Demonstrative Pronouns",
            "This (पास एकवचन), That (दूर एकवचन), These (पास बहुवचन), Those (दूर बहुवचन) का प्रयोग सही संदर्भ में करें।"
        )

# Load existing seed file
path = r"app\src\main\assets\lessons_seed.json"
with open(path, "r", encoding="utf-8") as f:
    data = json.load(f)

for q in data["lesson1Questions"]:
    ans = q["primaryAnswer"]
    hindi = q["hindiText"]
    
    # Generate word glosses for correct words + distractors
    all_words = ans.split() + (q.get("distractors") or [])
    for alt in (q.get("alternateAnswers") or []):
        all_words += alt.split()
        
    glosses = {}
    for w in all_words:
        clean = re.sub(r"[^\w']", "", w)
        if clean:
            glosses[clean] = get_word_gloss(clean)
            glosses[clean.lower()] = get_word_gloss(clean)
            glosses[clean.capitalize()] = get_word_gloss(clean)

    rule_title, rule_body = get_grammar_rule(hindi, ans)
    q["ruleTitle"] = rule_title
    q["grammarRule"] = rule_body
    q["wordGlosses"] = glosses
    
    # Tag info
    if "those" in ans.lower():
        q["tagKeyword"] = "Those (वे / वो)"
        q["categoryTag"] = "Plural Distance"
    elif "these" in ans.lower():
        q["tagKeyword"] = "These (ये / ये सब)"
        q["categoryTag"] = "Plural Near"
    elif "that" in ans.lower():
        q["tagKeyword"] = "That (वह / वो)"
        q["categoryTag"] = "Singular Distance"
    else:
        q["tagKeyword"] = "This (यह / ये)"
        q["categoryTag"] = "Singular Near"

with open(path, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print("Updated lessons_seed.json with rich glosses and grammar rules successfully!")
