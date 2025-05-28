package a3.client;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;
import org.joml.*;

import a3.MyGame;
import tage.*;
import tage.shapes.*;



public class GhostManager
{
	private MyGame game;
	private Vector<GhostAvatar> ghostAvatars = new Vector<GhostAvatar>();

	public GhostManager(VariableFrameRateGame vfrg)
	{	game = (MyGame)vfrg;
	}
	
	public void createGhostAvatar(UUID id, Vector3f position) throws IOException
	{	System.out.println("adding ghost with ID --> " + id);
		AnimatedShape s = game.getGhostShape();
		TextureImage t = game.getGhostTexture();
		GhostAvatar newAvatar = new GhostAvatar(id, s, t, position);
		Matrix4f initialScale = (new Matrix4f()).scaling(0.5f);
		newAvatar.setLocalScale(initialScale);
		ghostAvatars.add(newAvatar);
	}
	
	public void removeGhostAvatar(UUID id)
	{	GhostAvatar ghostAvatar = findAvatar(id);
		if(ghostAvatar != null)
		{	game.getEngine().getSceneGraph().removeGameObject(ghostAvatar);
			ghostAvatars.remove(ghostAvatar);
		}
		else
		{	System.out.println("tried to remove, but unable to find ghost in list");
		}
	}

	private GhostAvatar findAvatar(UUID id)
	{	GhostAvatar ghostAvatar;
		Iterator<GhostAvatar> it = ghostAvatars.iterator();
		while(it.hasNext())
		{	ghostAvatar = it.next();
			if(ghostAvatar.getID().compareTo(id) == 0)
			{	return ghostAvatar;
			}
		}		
		return null;
	}
	
	public void updateGhostAvatar(UUID id, Vector3f position)
	{	GhostAvatar ghostAvatar = findAvatar(id);
		if (ghostAvatar != null)
		{	ghostAvatar.setPosition(position);
			ghostAvatar.playAnimation("Walk", 0.5f, AnimatedShape.EndType.LOOP, 0);
		}
		else
		{	System.out.println("tried to update ghost avatar position, but unable to find ghost in list");
		}
	}
	
	public void updateGhostAvatarYaw(UUID ghostID, float rotation) {
        GhostAvatar ghostAvatar = findAvatar(ghostID);
        if (ghostAvatar != null) {
            Matrix4f yaw = new Matrix4f().rotateY(-rotation);
			ghostAvatar.setLocalRotation(yaw);
        } else {
            System.out.println("Can't find ghost!!");
        }
    }

	public Vector<GhostAvatar> getGhostAvatars() {
		return ghostAvatars;
	}
	

}
