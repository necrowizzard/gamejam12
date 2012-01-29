//TODO: CLEAN FBO!!!!!

package Renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

public class FBO {

	//private int fbo_index;
	private IntBuffer texture_index = BufferUtils.createIntBuffer(3);
	private IntBuffer depth_stencil_index = BufferUtils.createIntBuffer(3);
	private IntBuffer fbo_index = BufferUtils.createIntBuffer(3);
	private ByteBuffer texture = BufferUtils.createByteBuffer(16777216);
	private ByteBuffer texture_small = BufferUtils.createByteBuffer(16777216);
	
	private int sx, sy;
	private int smx, smy;
	
	private int refl_res = 2;
	private int anti_aliasing = 1;
	
	private int maxResolution;
	
	private boolean fboarb = false;
	private boolean fbocore = true;
	
	public FBO(int size_x, int size_y) {//, int reflection_resolution, int aa) {
		
		System.out.println("created fbo");
		
		refl_res = 2;
		anti_aliasing = 1;
		
		IntBuffer maxsize = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, maxsize);
		maxResolution = maxsize.get(0);
		
		//was not sure if integer rounding is always the same forever
		if ((size_x*anti_aliasing) >= maxResolution) {
			anti_aliasing = (int)Math.floor((double)maxResolution/(double)size_x);
			System.out.println("Anti aliasing reset to " + anti_aliasing);
		}
		if ((size_y*anti_aliasing) >= maxResolution) {
			anti_aliasing = (int)Math.floor((double)maxResolution/(double)size_y);
			System.out.println("Anti aliasing reset to " + anti_aliasing);
		}
		
		size_x *= anti_aliasing;
		size_y *= anti_aliasing;
		
		sx = size_x;
		sy = size_y;
		smx = size_x/refl_res; //possible to lower reflection scale here
		smy = size_y/refl_res;
		texture = BufferUtils.createByteBuffer(sx*sy*16);
		texture_small = BufferUtils.createByteBuffer(smx*smy*16);
		
		if (fboarb) fbocore = false;
		
		if (fbocore)
			initcore();
		else if (fboarb)
			initarb();
		else
			init();
	}
	
	public void updateFBOsize(int size_x, int size_y) {
		
		size_x *= anti_aliasing;
		size_y *= anti_aliasing;
		
		if (sx != size_x && sy != size_y) {
			//was not sure if integer rounding is always the same forever
			if ((size_x*anti_aliasing) >= maxResolution) {
				anti_aliasing = (int)Math.floor((double)maxResolution/(double)size_x);
				System.out.println("Anti aliasing reset to " + anti_aliasing);
			}
			if ((size_y*anti_aliasing) >= maxResolution) {
				anti_aliasing = (int)Math.floor((double)maxResolution/(double)size_y);
				System.out.println("Anti aliasing reset to " + anti_aliasing);
			}
			
			sx = size_x;
			sy = size_y;
			
			smx = size_x/refl_res; //TODO! if changed in constructor
			smy = size_y/refl_res;
			//System.out.println("resized: " + sx + "/" + sy);
			texture = BufferUtils.createByteBuffer(sx*sy*16);
			texture_small = BufferUtils.createByteBuffer(smx*smy*16);
			if (fbocore)
				initcore();
			else if (fboarb)
				initarb();
			else
				init();
		}
		
	}
	
	private void initarb() {
		//OLD... may try 3.0 or newer later
		
		//System.out.println("new fbo check version");
		
		if (!GLContext.getCapabilities().GL_ARB_framebuffer_object)  {
			//throw new RuntimeException("No GL_ARB_framebuffer_object supported");
			
			System.out.println("Swapping to FBO EXT");
			
			fboarb = false;
			fbocore = false;
			
			init();
			
			return;
		}
			
		
		//gen textures
		GL11.glGenTextures(texture_index);
		//generate FBO
		ARBFramebufferObject.glGenFramebuffers(fbo_index); 
		//gen depth
		ARBFramebufferObject.glGenRenderbuffers(depth_stencil_index);
		
		//1ST
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(0));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //GL_LINEAR
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 
			GL11.GL_RGB8, sx, sy, 0, 
			GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture);
		
		//Bind FBO
		ARBFramebufferObject.glBindFramebuffer(
				ARBFramebufferObject.GL_FRAMEBUFFER, 
				fbo_index.get(0));
		ARBFramebufferObject.glFramebufferTexture2D(
				ARBFramebufferObject.GL_FRAMEBUFFER, 
				ARBFramebufferObject.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, texture_index.get(0), 0);
		
		//Bind Depth
		ARBFramebufferObject.glBindRenderbuffer(
				ARBFramebufferObject.GL_RENDERBUFFER, 
				depth_stencil_index.get(0));
		ARBFramebufferObject.glRenderbufferStorage(
				ARBFramebufferObject.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, sx, sy);
		ARBFramebufferObject.glFramebufferRenderbuffer(
				ARBFramebufferObject.GL_FRAMEBUFFER,
				ARBFramebufferObject.GL_DEPTH_ATTACHMENT,
				ARBFramebufferObject.GL_RENDERBUFFER, depth_stencil_index.get(0));
		
		//OLD STENCIL
		/*GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);*/
		/*EXTFramebufferObject.glFramebufferRenderbufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, depth_stencil_index.get(0));*/
		
		//2ND (instead of stencil FBO - which didn't seem to work...)
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(1));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //GL_LINEAR
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, sx, sy, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture);

		// Bind FBO
		ARBFramebufferObject.glBindFramebuffer(
				ARBFramebufferObject.GL_FRAMEBUFFER, fbo_index.get(1));
		ARBFramebufferObject.glFramebufferTexture2D(
				ARBFramebufferObject.GL_FRAMEBUFFER,
				ARBFramebufferObject.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, texture_index.get(1), 0);

		// Bind Depth
		ARBFramebufferObject.glBindRenderbuffer(
				ARBFramebufferObject.GL_RENDERBUFFER,
				depth_stencil_index.get(1));
		/*EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, sx, sy);*/
		ARBFramebufferObject.glRenderbufferStorage(
				ARBFramebufferObject.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, sx, sy);
		ARBFramebufferObject.glFramebufferRenderbuffer(
				ARBFramebufferObject.GL_FRAMEBUFFER,
				ARBFramebufferObject.GL_DEPTH_ATTACHMENT,
				ARBFramebufferObject.GL_RENDERBUFFER,
				depth_stencil_index.get(1));
		
		//3RD 
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(2));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //test
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, smx, smy, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture_small);

		// Bind FBO
		ARBFramebufferObject.glBindFramebuffer(
				ARBFramebufferObject.GL_FRAMEBUFFER, fbo_index.get(2));
		ARBFramebufferObject.glFramebufferTexture2D(
				ARBFramebufferObject.GL_FRAMEBUFFER,
				ARBFramebufferObject.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, texture_index.get(2), 0);

		// Bind Depth
		ARBFramebufferObject.glBindRenderbuffer(
				ARBFramebufferObject.GL_RENDERBUFFER,
				depth_stencil_index.get(2));
		/*EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, smx, smy);*/
		ARBFramebufferObject.glRenderbufferStorage(
				ARBFramebufferObject.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, smx, smy);
		ARBFramebufferObject.glFramebufferRenderbuffer(
				ARBFramebufferObject.GL_FRAMEBUFFER,
				ARBFramebufferObject.GL_DEPTH_ATTACHMENT,
				ARBFramebufferObject.GL_RENDERBUFFER,
				depth_stencil_index.get(2));
		
		
		//ERROR CHECKING
		int framebuffer = ARBFramebufferObject.glCheckFramebufferStatus(ARBFramebufferObject.GL_FRAMEBUFFER);
		switch ( framebuffer ) {
			case ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE:
				break;
			case ARBFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT exception" );
			case ARBFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT exception" );
			case ARBFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER exception" );
			case ARBFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER exception" );
			default:
				throw new RuntimeException("Unexpected reply from glCheckFramebufferStatus: " + framebuffer);
		}
		
		//36061 == 0x8CDD == FRAMEBUFFER_UNSUPPORTED_EXT                    <- if no filter for texture defined
		
		//unbind
		ARBFramebufferObject.glBindFramebuffer(
				ARBFramebufferObject.GL_FRAMEBUFFER, 
				0);
	}
	
	private void initcore() {
		//OLD... may try 3.0 or newer later
		
		//System.out.println("new fbo check version");
		
		//CHECK version
		String version = GL11.glGetString(GL11.GL_VERSION);
				
		if (Integer.valueOf(version.substring(0,1)) < 3) {
			//throw new RuntimeException("Pre OpenGL 3.0 do not support core FBO.");
			
			System.out.println("Swapping to FBO ARB");
			
			fboarb = true;
			fbocore = false;
			
			initarb();
			
			return;
		}
		
		//gen textures
		GL11.glGenTextures(texture_index);
		//generate FBO
		GL30.glGenFramebuffers(fbo_index); 
		//gen depth
		GL30.glGenRenderbuffers(depth_stencil_index);
		
		//1ST
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(0));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //GL_LINEAR
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 
			GL11.GL_RGB8, sx, sy, 0, 
			GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture);
		
		//Bind FBO
		GL30.glBindFramebuffer(
				GL30.GL_FRAMEBUFFER, 
				fbo_index.get(0));
		GL30.glFramebufferTexture2D(
				GL30.GL_FRAMEBUFFER, 
				GL30.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, texture_index.get(0), 0);
		
		//Bind Depth
		GL30.glBindRenderbuffer(
				GL30.GL_RENDERBUFFER, 
				depth_stencil_index.get(0));
		GL30.glRenderbufferStorage(
				GL30.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, sx, sy);
		GL30.glFramebufferRenderbuffer(
				GL30.GL_FRAMEBUFFER,
				GL30.GL_DEPTH_ATTACHMENT,
				GL30.GL_RENDERBUFFER, depth_stencil_index.get(0));
		
		//OLD STENCIL
		/*GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);*/
		/*EXTFramebufferObject.glFramebufferRenderbufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, depth_stencil_index.get(0));*/
		
		//2ND (instead of stencil FBO - which didn't seem to work...)
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(1));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //GL_LINEAR
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, sx, sy, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture);

		// Bind FBO
		GL30.glBindFramebuffer(
				GL30.GL_FRAMEBUFFER, fbo_index.get(1));
		GL30.glFramebufferTexture2D(
				GL30.GL_FRAMEBUFFER,
				GL30.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, texture_index.get(1), 0);

		// Bind Depth
		GL30.glBindRenderbuffer(
				GL30.GL_RENDERBUFFER,
				depth_stencil_index.get(1));
		/*EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, sx, sy);*/
		GL30.glRenderbufferStorage(
				GL30.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, sx, sy);
		GL30.glFramebufferRenderbuffer(
				GL30.GL_FRAMEBUFFER,
				GL30.GL_DEPTH_ATTACHMENT,
				GL30.GL_RENDERBUFFER,
				depth_stencil_index.get(1));
		
		//3RD 
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(2));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //test
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, smx, smy, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture_small);

		// Bind FBO
		GL30.glBindFramebuffer(
				GL30.GL_FRAMEBUFFER, fbo_index.get(2));
		GL30.glFramebufferTexture2D(
				GL30.GL_FRAMEBUFFER,
				GL30.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, texture_index.get(2), 0);

		// Bind Depth
		GL30.glBindRenderbuffer(
				GL30.GL_RENDERBUFFER,
				depth_stencil_index.get(2));
		/*EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, smx, smy);*/
		GL30.glRenderbufferStorage(
				GL30.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, smx, smy);
		GL30.glFramebufferRenderbuffer(
				GL30.GL_FRAMEBUFFER,
				GL30.GL_DEPTH_ATTACHMENT,
				GL30.GL_RENDERBUFFER,
				depth_stencil_index.get(2));
		
		
		//ERROR CHECKING
		int framebuffer = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		switch ( framebuffer ) {
			case GL30.GL_FRAMEBUFFER_COMPLETE:
				break;
			case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT exception" );
			case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT exception" );
			case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER exception" );
			case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER exception" );
			default:
				throw new RuntimeException("Unexpected reply from glCheckFramebufferStatus: " + framebuffer);
		}
		
		//36061 == 0x8CDD == FRAMEBUFFER_UNSUPPORTED_EXT                    <- if no filter for texture defined
		
		//unbind
		GL30.glBindFramebuffer(
				ARBFramebufferObject.GL_FRAMEBUFFER, 
				0);
	}
	
	private void init() {
		//OLD... may try 3.0 or newer later
		
		//System.out.println("new fbo check version");
		
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) 
			throw new RuntimeException("No FBO Extension supported");
		
		//gen textures
		GL11.glGenTextures(texture_index);
		//generate FBO
		EXTFramebufferObject.glGenFramebuffersEXT(fbo_index); 
		//gen depth
		EXTFramebufferObject.glGenRenderbuffersEXT(depth_stencil_index);
		
		//1ST
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(0));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //GL_LINEAR
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 
			GL11.GL_RGB8, sx, sy, 0, 
			GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture);
		
		//Bind FBO
		EXTFramebufferObject.glBindFramebufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 
				fbo_index.get(0));
		EXTFramebufferObject.glFramebufferTexture2DEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 
				EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
				GL11.GL_TEXTURE_2D, texture_index.get(0), 0);
		
		//Bind Depth
		EXTFramebufferObject.glBindRenderbufferEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, 
				depth_stencil_index.get(0));
		EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, 
				GL11.GL_DEPTH_COMPONENT, sx, sy);
		EXTFramebufferObject.glFramebufferRenderbufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, depth_stencil_index.get(0));
		
		//OLD STENCIL
		/*GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);*/
		/*EXTFramebufferObject.glFramebufferRenderbufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, depth_stencil_index.get(0));*/
		
		//2ND (instead of stencil FBO - which didn't seem to work...)
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(1));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //GL_LINEAR
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, sx, sy, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture);

		// Bind FBO
		EXTFramebufferObject.glBindFramebufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo_index.get(1));
		EXTFramebufferObject.glFramebufferTexture2DEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
				GL11.GL_TEXTURE_2D, texture_index.get(1), 0);

		// Bind Depth
		EXTFramebufferObject.glBindRenderbufferEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				depth_stencil_index.get(1));
		/*EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, sx, sy);*/
		EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, 
				GL11.GL_DEPTH_COMPONENT, sx, sy);
		EXTFramebufferObject.glFramebufferRenderbufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				depth_stencil_index.get(1));
		
		//3RD 
		
		//bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(2));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //test
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, smx, smy, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, texture_small);

		// Bind FBO
		EXTFramebufferObject.glBindFramebufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo_index.get(2));
		EXTFramebufferObject.glFramebufferTexture2DEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
				GL11.GL_TEXTURE_2D, texture_index.get(2), 0);

		// Bind Depth
		EXTFramebufferObject.glBindRenderbufferEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				depth_stencil_index.get(2));
		/*EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, smx, smy);*/
		EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, 
				GL11.GL_DEPTH_COMPONENT, smx, smy);
		EXTFramebufferObject.glFramebufferRenderbufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				depth_stencil_index.get(2));
		
		
		//ERROR CHECKING
		int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT );
		switch ( framebuffer ) {
			case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
				break;
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
				throw new RuntimeException( "FrameBuffer: " + fbo_index
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception" );
			default:
				throw new RuntimeException( "Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer );
		}
		
		//36061 == 0x8CDD == FRAMEBUFFER_UNSUPPORTED_EXT                    <- if no filter for texture defined
		
		//unbind
		EXTFramebufferObject.glBindFramebufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 
				0);
	}
	
	//1.2% (happens often...)
	public void bind(int index) {
		if (!fboarb)
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo_index.get(index));
		else if (!fbocore)
			ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, fbo_index.get(index));
		else
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo_index.get(index));
		
		GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
		if (index == 2) GL11.glViewport(0, 0, smx, smy);
		else GL11.glViewport(0, 0, sx, sy);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT |  GL11.GL_STENCIL_BUFFER_BIT);
		
		GL11.glPushMatrix();
		
		
	}
	
	//0.8%
	public void unbind() {
		if (!fboarb)
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		else if (!fbocore)
			ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
		else
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
	
	public void bind_texture(int index) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_index.get(index));
	}
	
	/*public void bind_stencil_texture() {
		
		//needs to bind some kind of texture...
		
		//WAS ON!!! GL11.glBindTexture(GL11.GL_TEXTURE_2D, depth_stencil_index.get(0));
		
		
		
		
		/*EXTFramebufferObject.glBindFramebufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 
				fbo_index.get(0));
		
		
	}*/
	
	/*public void unbind_stencil_texture() {
		EXTFramebufferObject.glBindFramebufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 
				0);
	}*/
	
	public void unbind_texture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
}