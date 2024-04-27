package xyz.magicalstone.touchcontrol.skill;

import xyz.magicalstone.touchcontrol.skill.service.ServiceSkillAdapter;

import java.util.*;

public final class SkillRegistry {

    private static final Map<String, Skill> idToSkill = new HashMap<>();

    private static final Set<Skill> exportedSkills = new HashSet<>();

    private static final Map<String, Set<Skill>> descToSkill = new HashMap<>();

    public static Skill getSkillById(String name) {
        return idToSkill.get(name);
    }

    public static Skill findMostLikelySkillByDesc(String desc, Map<String, String> args) {
        Set<Skill> skillsWithDesc = descToSkill.get(desc);
        if (skillsWithDesc == null || skillsWithDesc.isEmpty()) {
            return null;
        }
        Set<String> argsKeys = args.keySet();
        for (Skill skill : skillsWithDesc) {
            if (skill.argsDesc.keySet().equals(argsKeys)) {
                return skill;
            }
        }
        return null;
    }

    public static void registerSkill(Skill skill, boolean exported) {
        if (skill instanceof ServiceSkillAdapter) {
            throw new IllegalArgumentException("A ServiceSkillAdapter shouldn't be registered.");
        }
        {
            idToSkill.put(skill.id, skill);
            if (exported) {
                exportedSkills.add(skill);
            }
            Set<Skill> skillsInSameDesc = descToSkill.computeIfAbsent(skill.desc, k -> new HashSet<>());
            skillsInSameDesc.add(skill);
        }
    }

    public static Set<Skill> getExportedSkills() {
        return Collections.unmodifiableSet(exportedSkills);
    }

    public static void refreshServiceSkills() {

    }

    private SkillRegistry() {

    }
}
