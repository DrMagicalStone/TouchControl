package xyz.magicalstone.touchcontrol.skill;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * A skill is a model of a "skill" in normal language. It provides an id for identifying and descriptions for AI activators to read.
 * The arg and result are both String to String maps while a key is the name of an item and the value is the item's value.
 */
public abstract class Skill {

    public enum ActivatorType {
        NON_AI, GPT
    }

    /**
     * For identifying a 
     */
    public final String id;

    public final String desc;

    public final Map<String, String> argsDesc;


    public Skill(String id, String desc, Map<String, String> argsDesc) {
        this.id = id;
        this.desc = desc;
        this.argsDesc = Collections.unmodifiableMap(argsDesc);
    }

    public Map<String, String> active(Map<String, String> args, ActivatorType activatorType) {
        return active(args);
    }

    protected abstract Map<String, String> active(Map<String, String> optimizedArgs);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill)) return false;
        Skill skill = (Skill) o;
        return Objects.equals(id, skill.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
