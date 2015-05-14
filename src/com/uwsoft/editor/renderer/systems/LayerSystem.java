package com.uwsoft.editor.renderer.systems;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.SnapshotArray;
import com.uwsoft.editor.renderer.conponents.CompositeTransformComponent;
import com.uwsoft.editor.renderer.conponents.LayerMapComponent;
import com.uwsoft.editor.renderer.conponents.NodeComponent;
import com.uwsoft.editor.renderer.conponents.ZindexComponent;
import com.uwsoft.editor.renderer.legacy.data.LayerItemVO;

public class LayerSystem extends IteratingSystem {

	private Comparator<Entity> comparator = new ZComparator();
	
	private ComponentMapper<ZindexComponent> zIndexMapper;
	private ComponentMapper<LayerMapComponent> layerMapper;
	private ComponentMapper<NodeComponent> nodeMapper;
	
	public LayerSystem() {
		super(Family.all(CompositeTransformComponent.class).get());
		zIndexMapper = ComponentMapper.getFor(ZindexComponent.class);
		layerMapper = ComponentMapper.getFor(LayerMapComponent.class);
		nodeMapper = ComponentMapper.getFor(NodeComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		NodeComponent nodeComponent = nodeMapper.get(entity);
		LayerMapComponent layerMapComponent = layerMapper.get(entity);
		updateLayers(nodeComponent.children,layerMapComponent);
		sort(nodeComponent.children);
	}
	
	private void updateLayers(SnapshotArray<Entity> children, LayerMapComponent layerMapComponent) {
		for (int i = 0; i < children.size; i++) {
			Entity entity = children.get(i);
			ZindexComponent zindexComponent = zIndexMapper.get(entity);
			zindexComponent.layerIndex = getlayerIndexByName(zindexComponent.layerName,layerMapComponent);
        }
	}

	private void sort(SnapshotArray<Entity> children) {
		children.sort(comparator);
	}
	
	private int getlayerIndexByName(String layerName, LayerMapComponent layerMapComponent) {
		 if(layerMapComponent == null){
			 return 0;
		 }
		 ArrayList<LayerItemVO> layers = layerMapComponent.layers;
		 for (int i = 0; i < layers.size(); i++) {
			 if (layers.get(i).layerName.equals(layerName)) {
				 return i;
			 }
		 }
		 return 0;
	 }
	
	private class ZComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity e1, Entity e2) {
        	ZindexComponent zIndexComponent1 = zIndexMapper.get(e1);
        	ZindexComponent zIndexComponent2 = zIndexMapper.get(e2);
        	return zIndexComponent1.layerIndex == zIndexComponent2.layerIndex ? Integer.signum(zIndexComponent1.zIndex - zIndexComponent2.zIndex) : Integer.signum(zIndexComponent1.layerIndex - zIndexComponent2.layerIndex);
            //return (int)Math.signum(pm.get(e1).z - pm.get(e2).z);
        }
    }

}