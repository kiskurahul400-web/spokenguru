import re
import json
import os

ocr_file = r"c:\Users\appuser\Downloads\Spoken_English_Guru_Index_Lesson_1_OCR.txt"
with open(ocr_file, "r", encoding="utf-8") as f:
    lines = f.readlines()

lessons = []
questions_1 = []

in_index = False
in_questions = False
in_answers = False

q_map = {}
ans_map = {}

for line in lines:
    line_clean = line.strip()
    if not line_clean:
        continue
    if "INDEX" in line_clean:
        in_index = True
        continue
    if "LESSON 1 — USE OF" in line_clean:
        in_index = False
        continue
    if "Translate the following sentences into English:" in line_clean:
        in_questions = True
        continue
    if "ANSWER KEY" in line_clean:
        in_questions = False
        in_answers = True
        continue

    if in_index:
        m = re.match(r"^(\d+)\.\s+(.*?)\s+(\d+)$", line_clean)
        if m:
            num = int(m.group(1))
            title = m.group(2).strip()
            page = int(m.group(3))
            lessons.append({
                "lessonNumber": num,
                "title": title,
                "pageNumber": page,
                "totalQuestions": 101 if num == 1 else 0,
                "isAvailable": True if num == 1 else False
            })

    elif in_questions:
        m = re.match(r"^(\d+)\.\s+(.*?)\s*\((.*?)\)$", line_clean)
        if m:
            q_num = int(m.group(1))
            hindi = m.group(2).strip()
            roman = m.group(3).strip()
            q_map[q_num] = (hindi, roman)
        else:
            m2 = re.match(r"^(\d+)\.\s+(.*)$", line_clean)
            if m2:
                q_num = int(m2.group(1))
                q_map[q_num] = (m2.group(2).strip(), "")

    elif in_answers:
        m = re.match(r"^(\d+)\.\s+(.*)$", line_clean)
        if m:
            q_num = int(m.group(1))
            ans = m.group(2).strip()
            ans_map[q_num] = ans

print(f"Parsed {len(lessons)} lessons.")
print(f"Parsed {len(q_map)} questions and {len(ans_map)} answers.")

key_hints = {
    "बगीचा": "garden = बगीचा",
    "चालाक": "clever = चालाक",
    "सब्ज़ी": "vegetable = सब्ज़ी",
    "गमले": "pots = गमले",
    "हँस": "laughing = हँस रहे",
    "मुरझा": "dead / withered = मुरझा गये",
    "प्रतिभाशाली": "talented = प्रतिभाशाली",
    "कठिन": "difficult / toughest = कठिन",
    "निर्णय": "decision = निर्णय",
    "कैंचियाँ": "scissors = कैंचियाँ",
    "दादा-दादी": "grandparents = दादा-दादी",
    "मेहमान": "guests = मेहमान",
    "घड़ी": "watch = घड़ी",
    "सपने": "dreams = सपने",
    "बुरे": "worst = सबसे बुरे",
    "चॉकलेट": "chocolates = चॉकलेट",
    "परफॉरमेन्स": "performance = प्रदर्शन",
    "अद्भुत": "amazing = अद्भुत / कमाल",
    "समीर": "Sameer (Friend's name)",
    "अमित": "Amit (Name)",
    "रोहन": "Rohan (Name)",
    "न्यूयॉर्क": "Newyork Building",
    "ट्रेन": "train = रेलगाड़ी",
    "दूध": "milk = दूध",
    "दुकान": "shop = दुकान"
}

formatted_questions = []
for num in sorted(q_map.keys()):
    hindi, roman = q_map[num]
    raw_ans = ans_map.get(num, "")
    
    # Check alternate answers
    if " / " in raw_ans:
        parts = [p.strip() for p in raw_ans.split(" / ")]
        primary = parts[0]
        alts = parts[1:]
    else:
        primary = raw_ans
        alts = []

    def clean_word(w):
        return re.sub(r"[^\w']", "", w).lower()

    words_in_ans = set(clean_word(w) for w in primary.split() if clean_word(w))
    for a in alts:
        words_in_ans.update(clean_word(w) for w in a.split() if clean_word(w))

    distractors = []
    candidates = ["these", "those", "this", "that", "are", "is", "was", "were", "my", "your", "there", "here"]
    for cand in candidates:
        if cand not in words_in_ans and cand not in distractors:
            distractors.append(cand)
        if len(distractors) >= 3:
            break

    hint = "Notice singular vs plural and near vs far (This/That vs These/Those)."
    for k, v in key_hints.items():
        if k in hindi:
            hint = v
            break

    formatted_questions.append({
        "questionNumber": num,
        "lessonNumber": 1,
        "hindiText": hindi,
        "romanText": roman,
        "primaryAnswer": primary,
        "alternateAnswers": alts,
        "distractors": distractors,
        "hint": hint
    })

seed_data = {
    "version": 1,
    "lessons": lessons,
    "lesson1Questions": formatted_questions
}

out_dir = r"c:\Users\appuser\Documents\GitHub\spokenguru\app\src\main\assets"
os.makedirs(out_dir, exist_ok=True)
seed_path = os.path.join(out_dir, "lessons_seed.json")
with open(seed_path, "w", encoding="utf-8") as f:
    json.dump(seed_data, f, ensure_ascii=False, indent=2)

print(f"Successfully generated {seed_path} with {len(formatted_questions)} questions and {len(lessons)} lessons.")
