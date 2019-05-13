package org.alora;


import org.alora.visitors.*;
import org.alora.visitors.definitions.NpcDefinition;
import org.alora.visitors.interactable.Character;
import org.alora.visitors.interactable.Npc;
import org.alora.visitors.interactable.Player;
import org.alora.visitors.nodes.CacheableNode;
import org.alora.visitors.nodes.Node;
import org.alora.visitors.nodes.NodeDeque;
import org.alora.visitors.nodes.RenderableNode;
import org.ethan.analyze.server.Server;
import org.ethan.analyze.server.ServerInfo;
import org.ethan.analyze.visitor.GraphVisitor;

import java.io.File;

@ServerInfo(serverName = "Alora", author = "Ethan", version = 1.0)
public class Core extends Server {

    public Core(String serverName) {
        super(serverName, new File("C:\\Users\\itset\\Downloads\\client(2)\\client.jar"));
    }

    @Override
    protected GraphVisitor[] getVisitors() {
        return new GraphVisitor[]{
                new Node(), new CacheableNode(), new RenderableNode(), new Canvas(),
                new Animable(), new HashTable(), new Cache(), new NodeDeque(),
                new LinkedList(), new AnimationSequence(), new Character(), new NpcDefinition(), new Player(), new Npc()
        };
    }

}
