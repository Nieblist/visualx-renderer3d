package fileFormats;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import logic3D.Face;
import logic3D.Vec3;
import logic3D.Vertex;
import modelViewer.Model;

public class SURMeshFormat {
	
	public static final String KEYWORD_ELEMENTGROUPS = "*ELEMENT GROUPS";
	public static final String KEYWORD_INCIDENCE	 = "*INCIDENCE";
	public static final String KEYWORD_COORDINATES 	 = "*COORDINATES";
	public static final String KEYWORD_FRONTVOLUME 	 = "*FRONT_VOLUME";
	
	
	public static Model openFile(RandomAccessFile file) throws IOException {
		int elementGroups = 0;
		int currentGroup = 0;
		
		String currentLine;
		double minX, maxX,
			   minY, maxY, 
			   minZ, maxZ, 
			   meanX, meanY, meanZ, meanR;
		
		int[] faceNumber = null;
		String[] groupType = null; 
		ArrayList< Vertex > vertices = new ArrayList< Vertex >();
		ArrayList< Integer[] > faces = new ArrayList< Integer[] >();
		
		Model newModel = new Model();
		
		file.seek(0);
		currentLine = file.readLine();
		
		while(currentLine != null) {
			switch (currentLine) {
				case KEYWORD_ELEMENTGROUPS:
					currentLine = file.readLine();
					currentLine = currentLine.replaceAll("\\s+", "");
					
					elementGroups = Integer.valueOf(currentLine);
					faceNumber = new int[elementGroups];
					groupType = new String[elementGroups];
					
					for (int i = 0; i < elementGroups; i++) {
						currentLine = file.readLine();
						currentLine = currentLine.toUpperCase();
						String[] groupValues = currentLine.split("\\s+");
						
						int group = Integer.valueOf(groupValues[0]);
						
						faceNumber[group-1] = Integer.valueOf(groupValues[1]);
						groupType[group-1]  = groupValues[2];
					}
					currentGroup = 1;
				break;
				case KEYWORD_INCIDENCE:
					for(; currentGroup <= elementGroups; currentGroup++) {
						currentLine = file.readLine();
						for(int i = 0; i < faceNumber[currentGroup - 1]; i++) {
							currentLine = file.readLine();
							currentLine = currentLine.replaceAll("^\\s+", "");
							String[] values = currentLine.split("\\s+");
							
							Integer[] nV = new Integer[3];
							nV[0] = Integer.valueOf(values[0]);
							nV[1] = Integer.valueOf(values[1]);
							nV[2] = Integer.valueOf(values[2]);
							
							faces.add(nV);
						}
					}
				break;
				case KEYWORD_COORDINATES:
					currentLine = file.readLine();
					currentLine = currentLine.replaceAll("\\s+", "");
					int vertexNumber = Integer.valueOf(currentLine);
					
					minX = Double.POSITIVE_INFINITY;
					maxX = Double.NEGATIVE_INFINITY;
					minY = Double.POSITIVE_INFINITY;
					maxY = Double.NEGATIVE_INFINITY;
					minZ = Double.POSITIVE_INFINITY;
					maxZ = Double.NEGATIVE_INFINITY;
					meanX = 0;
					meanY = 0;
					meanZ = 0;
					meanR = 0;
					
					for(int i = 0; i < vertexNumber; i++) {
						currentLine = file.readLine();
						currentLine = currentLine.replaceAll("^\\s+", "");
						String[] values = currentLine.split("\\s+");
						
						double x = Double.valueOf(values[1]);
						double y = Double.valueOf(values[2]);
						double z = Double.valueOf(values[3]);
						if (x < minX) { minX = x; }
						if (x > maxX) { maxX = x; }
						if (y < minY) { minY = y; }
						if (y > maxY) { maxY = y; }
						if (z < minX) { minZ = z; }
						if (z > maxX) { maxZ = z; }
						
						meanX += x;
						meanY += y;
						meanZ += z;
						
						Vertex v = new Vertex(x, y, z);
						
						meanR += v.position.norm();
						
						vertices.add(v);
					}
					
					meanX /= vertexNumber;
					meanY /= vertexNumber;
					meanZ /= vertexNumber;
					meanR /= vertexNumber;
		
					newModel.setBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
					newModel.setMeanPoint(meanX, meanY, meanZ);
					newModel.setMeanRadius(meanR);
				break;
				case KEYWORD_FRONTVOLUME:
					currentLine = file.readLine();
					currentLine = currentLine.replaceAll("^\\s+", "");
					String[] values = currentLine.split("\\s+");
					newModel.setBoundingBox(Double.valueOf(values[0]), Double.valueOf(values[2]), Double.valueOf(values[4]),
											Double.valueOf(values[1]), Double.valueOf(values[3]), Double.valueOf(values[5]));
				break;
			}
			currentLine = file.readLine();
		}
		
		newModel.setVertices(vertices);
		
		for(Integer[] f : faces) {
			int v1 = f[0] - 1;
			int v2 = f[1] - 1;
			int v3 = f[2] - 1;
			Vec3 normal = Vec3.cross(Vec3.sub(vertices.get(v2).position, vertices.get(v1).position),
									 Vec3.sub(vertices.get(v3).position, vertices.get(v1).position)).normalize();
			
			Face newFace = new Face(v1, v2, v3, normal);

			newModel.addFace(newFace);
		}
		
		return newModel;
	}
}
