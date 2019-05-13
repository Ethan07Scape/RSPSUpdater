package org.alora.visitors.interactable;


import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.Searcher;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.query.NumberQuery;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;


@VisitorInfo(hooks = {"x", "y", "interactingIndex", "animation", "orientation", "health", "maxHealth", "combatCycle"})
public class Character extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("RenderableNode"))
                && cn.fieldCount("[B") == 1
                && cn.fieldCount("Z") > 1;
    }

    @Override
    public void visit() {
        visitAll(new PositionHooks());
        visitAll(new InteractingIndex());
        findHealthHooks();
        findCombatCycle();

    }

    public void findHealthHooks() {
        for (ClassNode cn : getUpdateEngine().getClassNodes().values()) {
            final MethodNode m = this.getFirstMethod(cn, methodNode -> methodNode.instructions.size() < 1000
                    && getNewSearcher(methodNode).findIntValue(BIPUSH, 85) != -1
                    && getNewSearcher(methodNode).findIntValue(SIPUSH, 160) != -1);
            if (m != null) {
                Searcher searcher = getNewSearcher(m);
                FieldInsnNode fieldInsnNode;
                for (int i = 0; i < 10; i++) {
                    fieldInsnNode = searcher.getField(searcher.findSingleOpcode(GETFIELD, i));
                    if (fieldInsnNode != null) {
                        if (fieldInsnNode.owner.equals(clazz("Character"))) {
                            if (!containsKey("health")) {
                                add(new FieldHookFrame("health", fieldInsnNode));
                            } else if (!containsKey("maxHealth")) {
                                add(new FieldHookFrame("maxHealth", fieldInsnNode));
                            }
                        }
                    }
                }
            }
        }
    }

    private Searcher getNewSearcher(MethodNode m) {
        return new Searcher(m);
    }

    public void findCombatCycle() {
        for (ClassNode cn : getUpdateEngine().getClassNodes().values()) {
            final MethodNode m = getFirstMethod(cn,
                    methodNode -> methodNode.instructions.size() > 500
                            && getNewSearcher(methodNode).findIntValue(BIPUSH, -11) != -1
                            && getNewSearcher(methodNode).findIntValue(BIPUSH, -12) != -1
                            && getNewSearcher(methodNode).findIntValue(BIPUSH, -28) != -1);
            if (m != null) {
                Searcher search = new Searcher(m);
                FieldInsnNode fin = search.getField(search.findPattern(new int[]{GETFIELD, ICONST_M1, IXOR, GETSTATIC}, 1, 0));
                if (fin != null) {
                    add(new FieldHookFrame("combatCycle", fin));
                }
            }
        }
    }

    private class PositionHooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            if (block.count(new NumberQuery(SIPUSH, 13184)) > 0) {
                block.tree().accept(new NodeVisitor() {
                    public void visitJump(JumpNode jn) {
                        String name = null;
                        if (jn.opcode() == IF_ICMPGE) {
                            name = "x";
                        } else if (jn.opcode() == IF_ICMPLT) {
                            name = "y";
                        }
                        if (name == null || containsKey(name)) {
                            return;
                        }
                        FieldMemberNode fmn = jn.firstField();
                        if (fmn == null) {
                            return;
                        }
                        add(new FieldHookFrame(name, fmn.fin()));
                        added++;
                    }
                });
            }
        }
    }

    private class InteractingIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visit(AbstractNode n) {
                    if (n.opcode() == AALOAD) {
                        FieldMemberNode fmn = n.firstField();
                        if (fmn != null && fmn.desc().equals("[" + desc("Npc"))) {
                            fmn = (FieldMemberNode) n.layer(GETFIELD);
                            if (fmn != null && fmn.owner().equals(getCn().name) && fmn.desc().equals("I")) {
                                add(new FieldHookFrame("interactingIndex", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
  /*
    private class Animation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(clazz("AnimationSequence")) && fmn.desc().equals("I")) {
                        fmn = (FieldMemberNode) fmn.layer(INVOKESTATIC, GETFIELD);
                        if (fmn != null) {
                            //  System.out.println("Found it");
                            FieldHook fh = new FieldHook("animation", fmn.fin());
                            addHook(fh);
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Orientation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(getCn().name)) {
                        if (fmn.layer(IMUL, IAND, D2I, DMUL, INVOKESTATIC, I2D) != null) {
                            addHook(new FieldHook("orientation", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }*/
}