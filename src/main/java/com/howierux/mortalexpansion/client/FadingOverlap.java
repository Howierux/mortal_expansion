package com.howierux.mortalexpansion.client;

import com.mojang.blaze3d.vertex.*;
import java.nio.ByteBuffer;
import java.util.*;

/** Resolve coplanar overlap only, without turning off sorting for the rest of a model. */
public final class FadingOverlap {
    private static final float EPSILON=0.00001F;
    private static final float MAX_BIAS=1F/256F;
    private record Cell(int axis,int plane,int u,int v){}
    private record Face(int axis,int sign,float plane,float minU,float maxU,float minV,float maxV){}
    private FadingOverlap(){}
    public static VertexSorting prepare(MeshData mesh,VertexSorting sorting){
        var draw=mesh.drawState();
        if(draw.mode()!=VertexFormat.Mode.QUADS || draw.format().getOffset(VertexFormatElement.POSITION)<0)return sorting;
        int count=draw.vertexCount()/4, stride=draw.format().getVertexSize();
        int positionOffset=draw.format().getOffset(VertexFormatElement.POSITION);
        var vertices=mesh.vertexBuffer();
        Face[] faces=new Face[count];int[] parent=new int[count];
        Map<Cell,List<Integer>> cells=new HashMap<>();boolean overlap=false;
        for(int i=0;i<count;i++){
            parent[i]=i;Face face=face(vertices,i,stride,positionOffset);faces[i]=face;if(face==null)continue;
            // Native chest/dust quads fit in these cells. Bound work for arbitrary modded meshes.
            if(face.maxU-face.minU>2 || face.maxV-face.minV>2)continue;
            int plane=Math.round(face.plane/(EPSILON*2));
            Set<Integer> candidates=new HashSet<>();
            for(int u=(int)Math.floor(face.minU);u<=(int)Math.floor(face.maxU);u++)
                for(int v=(int)Math.floor(face.minV);v<=(int)Math.floor(face.maxV);v++){
                    for(int p=plane-1;p<=plane+1;p++){
                        var bucket=cells.get(new Cell(face.axis,p,u,v));
                        if(bucket!=null)candidates.addAll(bucket);
                    }
                    var bucket=cells.computeIfAbsent(new Cell(face.axis,plane,u,v),key->new ArrayList<>());
                    if(bucket.size()<64)bucket.add(i);
                }
            for(int j:candidates){
                Face other=faces[j];
                if(face.sign==other.sign && Math.abs(face.plane-other.plane)<EPSILON
                        && Math.min(face.maxU,other.maxU)-Math.max(face.minU,other.minU)>EPSILON
                        && Math.min(face.maxV,other.maxV)-Math.max(face.minV,other.minV)>EPSILON){
                    int a=root(parent,i),b=root(parent,j);if(a!=b){parent[a]=b;overlap=true;}
                }
            }
        }
        if(!overlap)return sorting;
        Map<Integer,List<Integer>> grouped=new HashMap<>();
        for(int i=0;i<count;i++)if(faces[i]!=null)grouped.computeIfAbsent(root(parent,i),key->new ArrayList<>()).add(i);
        var groups=grouped.values().stream().filter(group->group.size()>1).toList();
        for(var group:groups)for(int rank=1;rank<group.size();rank++){
            int index=group.get(rank);Face face=faces[index];
            float bias=MAX_BIAS*rank/(group.size()-1)*face.sign;
            for(int vertex=0;vertex<4;vertex++){
                int offset=(index*4+vertex)*stride+positionOffset+face.axis*4;
                vertices.putFloat(offset,vertices.getFloat(offset)+bias);
            }
        }
        return centers->{
            int[] order=sorting.sort(centers);int[] positions=new int[count];
            for(int i=0;i<count;i++)positions[order[i]]=i;
            // Keep normal distance sorting except where the same plane really overlaps.
            for(var group:groups){
                int[] slots=group.stream().mapToInt(index->positions[index]).sorted().toArray();
                for(int i=0;i<slots.length;i++)order[slots[i]]=group.get(i);
            }
            return order;
        };
    }
    private static int root(int[] parent,int index){
        while(parent[index]!=index){parent[index]=parent[parent[index]];index=parent[index];}return index;
    }
    private static Face face(ByteBuffer data,int index,int stride,int positionOffset){
        float[][] p=new float[4][3];
        for(int v=0;v<4;v++)for(int a=0;a<3;a++){
            p[v][a]=data.getFloat((index*4+v)*stride+positionOffset+a*4);if(!Float.isFinite(p[v][a]))return null;
        }
        for(int axis=0;axis<3;axis++){
            if(Math.abs(p[1][axis]-p[0][axis])>EPSILON || Math.abs(p[2][axis]-p[0][axis])>EPSILON || Math.abs(p[3][axis]-p[0][axis])>EPSILON)continue;
            int u=(axis+1)%3,v=(axis+2)%3;
            float cross=(p[1][u]-p[0][u])*(p[2][v]-p[0][v])-(p[1][v]-p[0][v])*(p[2][u]-p[0][u]);
            if(Math.abs(cross)<EPSILON)continue;
            float minU=p[0][u],maxU=minU,minV=p[0][v],maxV=minV;
            for(int j=1;j<4;j++){minU=Math.min(minU,p[j][u]);maxU=Math.max(maxU,p[j][u]);minV=Math.min(minV,p[j][v]);maxV=Math.max(maxV,p[j][v]);}
            return new Face(axis,cross>0?1:-1,p[0][axis],minU,maxU,minV,maxV);
        }
        return null;
    }
}
