
/***********************************************************************
 
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *** The Mega Super Awesome Visuals Company ***
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of MSA Visuals nor the names of its contributors 
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
 * ***********************************************************************/ 


package msafluid;

import java.util.Arrays;

import android.util.Log;

/**
 * this is a class for solving real-time fluid dynamics simulations based on Navier-Stokes equations 
 * and code from Jos Stam's paper "Real-Time Fluid Dynamics for Games" http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf
 * Other useful resources and implementations I looked at while building this lib: 
 * Mike Ash (C) - http://mikeash.com/?page=pyblog/fluid-simulation-for-dummies.html
 * Alexander McKenzie (Java) - http://www.multires.caltech.edu/teaching/demos/java/stablefluids.htm
 * Pierluigi Pesenti (AS3 port of Alexander's) - http://blog.oaxoa.com/2008/01/21/actionscript-3-fluids-simulation/
 * Gustav Taxen (C) - http://www.nada.kth.se/~gustavt/fluids/
 * Dave Wallin (C++) - http://nuigroup.com/touchlib/ (uses portions from Gustav's)
 * 
 * 
 * @example MSAFluid 
 * @author Memo Akten
 * 
 */
/***********************************************************************
- 
- Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
- *** The Mega Super Awesome Visuals Company ***
- * All rights reserved.
- *
- * Redistribution and use in source and binary forms, with or without
- * modification, are permitted provided that the following conditions are met:
- *
- *     * Redistributions of source code must retain the above copyright
- *       notice, this list of conditions and the following disclaimer.
- *     * Redistributions in binary form must reproduce the above copyright
- *       notice, this list of conditions and the following disclaimer in the
- *       documentation and/or other materials provided with the distribution.
- *     * Neither the name of MSA Visuals nor the names of its contributors 
- *       may be used to endorse or promote products derived from this software
- *       without specific prior written permission.
- *
- * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
- * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
- * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
- * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
- * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
- * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
- * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
- * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
- * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
- * OF THE POSSIBILITY OF SUCH DAMAGE. 
- *
- * ***********************************************************************/ 

public class MSAFluidSolver2DINT {
	public final int[]	r;
	public final int[]	g;
	public final int[]	b;
	
	public final int[]	u;
	public final int[]	v;

	public final int[]	rOld;
	public final int[]	gOld;
	public final int[]	bOld;
	
	public final int[]	uOld;
	public final int[]	vOld;

	public final String VERSION = "1.3.0";

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public String version() {
		return VERSION;
	}

	final static int    FLUID_DEFAULT_NX                    = 100;
	final static int    FLUID_DEFAULT_NY                    = 100;
	final static int    FLUID_DEFAULT_DT_T                  = 1;
	final static int    FLUID_DEFAULT_DT_B                  = 1;
	final static int    FLUID_DEFAULT_VISC_T                = 1;
	final static int    FLUID_DEFAULT_VISC_B                = 10000;
	final static int    FLUID_DEFAULT_FADESPEED_T           = 0;
	final static int    FLUID_DEFAULT_FADESPEED_B           = 0;
	final static int    FLUID_DEFAULT_SOLVER_ITERATIONS	    = 10;

	
	/**
	 * Constructor to initialize solver and setup number of cells
	 * @param NX number of cells in X direction
	 * @param NY number of cells in Y direction
	 */
	public MSAFluidSolver2DINT(int NX, int NY) {
		
		_isInited = false;
		setDeltaT(FLUID_DEFAULT_DT_T, FLUID_DEFAULT_DT_B);
		setFadeSpeed(FLUID_DEFAULT_FADESPEED_T, FLUID_DEFAULT_FADESPEED_B  );
		setSolverIterations(FLUID_DEFAULT_SOLVER_ITERATIONS);
		
		_NX = NX;
		_NY = NY;
		_numCells = (_NX + 2) * (_NY + 2);
		
		width		= getWidth();
		height		= getHeight();
		invWidth	= 1;
		invHeight	= 1;
		
		
		
		r    = new int[_numCells];
		rOld = new int[_numCells];

		
		g    = new int[_numCells];
		gOld = new int[_numCells];
		
		b    = new int[_numCells];
		bOld = new int[_numCells];
		
		u    = new int[_numCells];
		uOld = new int[_numCells];
		v    = new int[_numCells];
		vOld = new int[_numCells];

		_tmp = new int[_numCells];

		
		
		
		
		reset();
		
		
		

		
		
		
		
		
		enableRGB(false);
	}

	
	/**
	 * (OPTIONAL SETUP) re-initialize solver and setup number of cells
	 * @param NX number of cells in X direction
	 * @param NY number of cells in X direction
	 * @return instance of MSAFluidSolver2D for further configuration
	 */
	public MSAFluidSolver2DINT setup(int NX, int NY) {
		
		return this;
	}
	
	
	/**
	 * (OPTIONAL SETUP) set timestep
	 * @param dt timestep
	 * @return instance of MSAFluidSolver2D for further configuration
	 */
	public MSAFluidSolver2DINT setDeltaT(int dt_t, int dt_b) {
		_dt_t = dt_t;
		_dt_b = dt_b;
		return this;	
	}

	
	/**
	 * (OPTIONAL SETUP) set how quickly the fluid dye dissipates and fades out 
	 * @param fadeSpeed (0...1)
	 * @return instance of MSAFluidSolver2D for further configuration
	 */
	public MSAFluidSolver2DINT setFadeSpeed(int fadeSpeed_t, int fadeSpeed_b) {
		_fadeSpeed_t = fadeSpeed_t;
		_fadeSpeed_b = fadeSpeed_b;
		return this;	
	}

	
	/**
	 * (OPTIONAL SETUP) set number of iterations for solver (higher is slower but more accurate) 
	 * @param solverIterations
	 * @return instance of MSAFluidSolver2D for further configuration
	 */	
	public MSAFluidSolver2DINT setSolverIterations(int solverIterations) {
		_solverIterations = solverIterations;
		return this;	
	}
	
	/**
	 * (OPTIONAL SETUP) set whether solver should work with monochrome dye (default) or RGB
	 * @param isRGB true or false
	 * @return instance of MSAFluidSolver2D for further configuration
	 */		
	public MSAFluidSolver2DINT enableRGB(boolean isRGB) {
		_isRGB = isRGB;
		return this;
	}
	
	/**
	 * (OPTIONAL SETUP) set viscosity
	 * @param newVisc
	 * @return instance of MSAFluidSolver2D for further configuration
	 */		
	public MSAFluidSolver2DINT setVisc(int newVisc_T, int newVisc_B) {
		visc_t = newVisc_T;
		visc_b = newVisc_B;
		return this;
	}

	
	/**
	 * destroy solver and release all memory
	 */	
	public void destroy() {
		_isInited = false;
	}
	
	
	/**
	 * initialize solver (remove all velocities and dye)
	*/	
	public void reset() {
		destroy();
		_isInited = true;
		
//		r    = new float[_numCells];
		Arrays.fill(r, 0);
//		rOld = new float[_numCells];
		Arrays.fill(rOld, 0);

		
//		g    = new float[_numCells];
		Arrays.fill(g, 0);
//		gOld = new float[_numCells];
		Arrays.fill(gOld, 0);
		
//		b    = new float[_numCells];
		Arrays.fill(b, 0);
//		bOld = new float[_numCells];
		Arrays.fill(bOld, 0);
		
//		u    = new float[_numCells];
		Arrays.fill(u, 0);
//		uOld = new float[_numCells];
		Arrays.fill(uOld, 0);
//		v    = new float[_numCells];
		Arrays.fill(v, 0);
//		vOld = new float[_numCells];
		Arrays.fill(vOld, 0);
//		for (int i = 0; i < _numCells; i++) {
//			u[i] = uOld[i] = v[i] = vOld[i] = 0.0f;
//			r[i] = rOld[i] = g[i] = gOld[i] = b[i] = bOld[i] = 0;
//		}
	}
	
	/**
	 * (INFO) get fluid cell index for (i,j) cell coordinates
	 * @param i fluid cell index in x direction
	 * @param j fluid cell index in y direction
	 * @return cell index (to be used in r, g, b, u, v arrays)
	 */		
	public int getIndexForCellPosition(int i, int j) {
		if(i < 1) i=1; else if(i > _NX) i = _NX;
		if(j < 1) j=1; else if(j > _NY) j = _NY;
		return ((i) + (_NX + 2)  *(j));
	}
	
	/**
	 * (INFO) get fluid cell index for normalized (x, y) coordinates
	 * @param x 0...1 normalized x position
	 * @param y 0...1 normalized y position
	 * @return cell index (to be used in r, g, b, u, v arrays)
	 */		
	public int getIndexForNormalizedPosition(float x, float y) {
		return getIndexForCellPosition((int)Math.floor(x * (_NX+2)), (int)Math.floor(y * (_NY+2)));
	}

	
	/**
	 * (INFO) whether the solver has been setup or not
	 */		
	public boolean isInited() {
		return _isInited;
	}
	
	
	/**
	 * (INFO) return total number of cells (_NX+2) * (_NY+2)
	*/
	public int getNumCells() {
		return _numCells;
	}
	
	/**
	 * (INFO) return number of cells in x direction (_NX+2)
	*/
	public int getWidth() {
		return _NX + 2;
	}
	
	/**
	 * (INFO) return number of cells in y direction (_NY+2)
	*/
	public int getHeight() {
		return _NY + 2;
	}
		
	/**
	 * (INFO) return viscosity
	*/
	public float getVisc() {
		return (float)visc_t/(float)visc_b;
	}
	
	
	
	
	public void addForceAtPos(float x, float y, float vx, float vy) {
		int i = (int) (x * _NX + 2);
		int j = (int) (y * _NY + 2);
		if(i<1 || i>_NX || j<1 || j>_NY) return;
		addForceAtCell(i, j, vx, vy);
	}
	
	public void addForceAtPosInterp(float x, float y, float vx, float vy) {
		float rI = x * _NX + 2;
		float rJ = y * _NY + 2;
		int i1 = (int) (x * _NX + 2);
		int i2 = (rI - i1 < 0) ? (int) (x * _NX + 3) : (int) (x* _NX + 1);
		int j1 = (int) (y * _NY + 2);
		int j2 = (rJ - j1 < 0) ? (int) (y * _NY + 3) : (int) (y* _NY + 1);
		
		float diffx = (1-(rI-i1));
		float diffy = (1-(rJ-j1));
		
		float vx1 = vx * diffx*diffy;
		float vy1 = vy * diffy*diffx;
		
		float vx2 = vx * (1-diffx)*diffy;
		float vy2 = vy * diffy*(1-diffx);
		
		float vx3 = vx * diffx*(1-diffy);
		float vy3 = vy * (1-diffy)*diffx;
		
		float vx4 = vx * (1-diffx)*(1-diffy);
		float vy4 = vy * (1-diffy)*(1-diffx);
		
		/*
D/MSAFluidSolver(13634): Adding force 1 (orig:dx2.1063147 dy:-0.9879706  ): [49,19]: dx: 0.026681103  dy:-0.012514818 diffx:0.0930748 diffy:0.13609695
D/MSAFluidSolver(13634): Adding force 2 (orig:dx2.1063147 dy:-0.9879706  ): [48,19]: dx: 1.9102699  dy:-0.12194498 diffx:0.9069252 diffy:0.12342976
D/MSAFluidSolver(13634): Adding force 3 (orig:dx2.1063147 dy:-0.9879706  ): [49,18]: dx: 0.16936372  dy:-0.8535108 diffx:0.080407605 diffy:0.86390305
D/MSAFluidSolver(13634): Adding force 4 (orig:dx2.1063147 dy:-0.9879706  ): [48,18]: dx: 1.650288  dy:-0.77407044 diffx:0.7834954 diffy:0.7834954

		 */
		
		if(i1<2 || i1>_NX-1 || j1<2 || j1>_NY-1) return;

		Log.d("MSAFluidSolver", "Adding force 1 (orig:dx"+vx+" dy:"+vy+"  ): ["+i1+","+j1+"]: dx: "+vx1+"  dy:"+vy1+" diffx:"+diffx+" diffy:"+diffy);
		Log.d("MSAFluidSolver", "Adding force 2 (orig:dx"+vx+" dy:"+vy+"  ): ["+i2+","+j1+"]: dx: "+vx2+"  dy:"+vy2+" diffx:"+(1-diffx)+" diffy:"+diffy*(1-diffx));
		Log.d("MSAFluidSolver", "Adding force 3 (orig:dx"+vx+" dy:"+vy+"  ): ["+i1+","+j2+"]: dx: "+vx3+"  dy:"+vy3+" diffx:"+diffx*(1-diffy)+" diffy:"+(1-diffy));
		Log.d("MSAFluidSolver", "Adding force 4 (orig:dx"+vx+" dy:"+vy+"  ): ["+i2+","+j2+"]: dx: "+vx4+"  dy:"+vy4+" diffx:"+(1-diffx)*(1-diffy)+" diffy:"+(1-diffx)*(1-diffy));
		addForceAtCell(i1, j1, vx1, vy1);
		addForceAtCell(i2, j1, vx2, vy2);
		addForceAtCell(i1, j2, vx3, vy3);
		addForceAtCell(i2, j2, vx4, vy4);

	}
	
	public void addForceAtCell(int i, int j, float vx, float vy) {
		//	if(safeToRun()){
		int index = ((i) + (_NX + 2)  *(j));
		uOld[index] += vx;
		vOld[index] += vy;
		//		unlock();
		//	}
	}
	
	
	public void addColorAtPos(float x, float y, float r, float g, float b) {
		int i = (int) (x * _NX + 1);
		int j = (int) (y * _NY + 1);
		if(i<0 || i>_NX+1 || j<0 || j>_NY+1) return;
		addColorAtCell(i, j, r, g, b);	
	}
	
	public void addColorAtCell(int i, int j, float r, float g, float b) {
		//	if(safeToRun()){
		int index = ((i) + (_NX + 2)  *(j));
		rOld[index] += r;
		if(_isRGB) {
			gOld[index] += g;
			bOld[index] += b;
		}
		//		unlock();
		//	}
	}

	
	/*	
		public void getInfoAtPos(float x, float y, PVector vel, PVector col) {
			int i= (int)(x * (_NX+2));
			int j= (int)(y * (_NY+2));
			getInfoAtCell(i, j, vel, col);
		}
		
		public void getInfoAtCell(int i, int j,  PVector vel, PVector col) {
			if(i<0) i = 0; else if(i > _NX+1) i = _NX+1;
			if(j<0) j = 0; else if(j > _NY+1) j = _NY+1;
			getInfoAtCell(FLUID_IX(i, j), vel, col);
		}
		
		
		public void getInfoAtCell(int i,  PVector vel, PVector col) {
			//	if(safeToRun()){
			if(vel != null) vel.set(u[i] * _invNX, v[i] * _invNY, 0);
			if(col != null) {
				if(_isRGB) col.set(r[i], g[i], b[i]);
				else col.set(r[i], r[i], r[i]);
			}
			//		unlock();
			//	}
		}
		
		
		
		public void addForceAtPos(float x, float y, float vx, float vy) {
			int i = (int) (x * _NX + 1);
			int j = (int) (y * _NY + 1);
			if(i<0 || i>_NX+1 || j<0 || j>_NY+1) return;
			addForceAtCell(i, j, vx, vy);
		}
		
		public void addForceAtCell(int i, int j, float vx, float vy) {
			//	if(safeToRun()){
			int index = FLUID_IX(i, j);
			uOld[index] += vx * _NX;
			vOld[index] += vy * _NY;
			//		unlock();
			//	}
		}
		
		
		public void addColorAtPos(float x, float y, float r, float g, float b) {
			int i = (int) (x * _NX + 1);
			int j = (int) (y * _NY + 1);
			if(i<0 || i>_NX+1 || j<0 || j>_NY+1) return;
			addColorAtCell(i, j, r, g, b);	
		}
		
		public void addColorAtCell(int i, int j, float r, float g, float b) {
			//	if(safeToRun()){
			int index = FLUID_IX(i, j);
			rOld[index] += r;
			if(_isRGB) {
				gOld[index] += g;
				bOld[index] += b;
			}
			//		unlock();
			//	}
		}
		
		public void randomizeColor() {
			for(int i=0; i< getWidth(); i++) {
				for(int j=0; j< getHeight(); j++) {
					int index = FLUID_IX(i, j);
					r[index] = rOld[index] = random(0, 1);
					if(_isRGB) {
						g[index] = gOld[index] = random(0, 1);
						b[index] = bOld[index] = random(0, 1);
					}
				} 
			}
		}
	*/	
			
	/**
	 * this must be called once every frame to move the solver one step forward 
	 * i.e. in your sketch draw() method
	*/
	public void update() {
//		ADD_SOURCE_UV();
		addSourceUV();
		
		swapU();
		swapV();
		
//		DIFFUSE_UV();
		diffuseUV(0, visc_t, visc_b);
		
		project(u, v, uOld, vOld);
		
		swapU(); 
		swapV();
		
		advect(1, u, uOld, uOld, vOld);
		advect(2, v, vOld, uOld, vOld);
		
		project(u, v, uOld, vOld);
		
		if(_isRGB) {
			//ADD_SOURCE_RGB();
			addSourceRGB();
			swapRGB();
			
			//DIFFUSE_RGB();
			diffuseRGB(0, visc_t, visc_b);
			swapRGB();
			
			//ADVECT_RGB();
			advectRGB(0, u, v);
			
			fadeRGB();
		} else {
			addSource(r, rOld);
			swapR();
			
			diffuse(0, r, rOld, 1, 1);
			swapRGB();
			
			advect(0, r, rOld, u, v);	
			fadeR();
		}
	}

	
	protected void fadeR() {
		
		for (int i = 0; i < _numCells; i++) {
			// clear old values
			uOld[i] = vOld[i] = 0; 
			rOld[i] = 0;
			// fade out old
			r[i] = (r[i] * _fadeSpeed_t)/_fadeSpeed_b;
		}
	}
	
	
	protected void fadeRGB() {
		Arrays.fill(uOld, 0);
		Arrays.fill(vOld, 0);
		Arrays.fill(rOld, 0);
		Arrays.fill(gOld, 0);
		Arrays.fill(bOld, 0);
		
		for (int i = 0; i < _numCells; i++) {
			r[i] = (r[i] * _fadeSpeed_t)/_fadeSpeed_b;
			g[i] = (g[i] * _fadeSpeed_t)/_fadeSpeed_b;
			b[i] = (b[i] * _fadeSpeed_t)/_fadeSpeed_b;
			
		}
	}
	
	
	protected void addSourceUV() {
		for (int i = 0; i < _numCells; i++) {
			u[i] += (_dt_t * uOld[i])/_dt_b;
			v[i] += (_dt_t * vOld[i])/_dt_b;
		}
	}
	
	protected void addSourceRGB() {
		for (int i = 0; i < _numCells; i++) {
			r[i] += (_dt_t * rOld[i])/_dt_b;
			g[i] += (_dt_t * gOld[i])/_dt_b;
			b[i] += (_dt_t * bOld[i])/_dt_b;		
		}
	}
	
	
	
	protected void addSource(int[] x, int[] x0) {
		for (int i = 0; i < _numCells; i++) {
			x[i] += (_dt_t * x0[i]) / _dt_b;
		}
	}
	
	
	protected void advect(int b, int[] _d, int[] d0, int[] du, int[] dv) {
		int i0, j0, i1, j1;
		int x, y, s0, t0, s1, t1, dt0_t, dt0_b;
		
		dt0_t = _dt_t * _NX;
		dt0_b = _dt_b;
		
		for (int i = 1; i <= _NX; i++) {
			for (int j = 1; j <= _NY; j++) {
				x = i - (dt0_t * du[((i) + (_NX + 2)  *(j))])/dt0_b;//
				y = j - (dt0_t * dv[((i) + (_NX + 2)  *(j))])/dt0_b;
				
				if (x > _NX + 1) x = _NX; //eeeh?
				if (x < 1)     x = 1;
				
				i0 = (int) x;
				i1 = i0 + 1;
				
				if (y > _NY + 1) y = _NY; //eeeeeh?
				if (y < 1)     y = 1;
				
				j0 = (int) y;
				j1 = j0 + 1;
				
				s1 = x - i0;
				s0 = 1 - s1;
				t1 = y - j0;
				t0 = 1 - t1;
				
				_d[((i) + (_NX + 2)  *(j))] = s0 * (t0 * d0[((i0) + (_NX + 2)  *(j0))] + t1 * d0[((i0) + (_NX + 2)  *(j1))])
				+ s1 * (t0 * d0[((i1) + (_NX + 2)  *(j0))] + t1 * d0[((i1) + (_NX + 2)  *(j1))]);
				
			}
		}
	}
	
	protected void advectRGB(int bound, int[] du, int[] dv) {
		int i0, j0, i1, j1;
		int x, y, s0, t0, s1, t1, dt0_t, dt0_b;
		
		dt0_t = _dt_t * _NX;
		dt0_b = _dt_b;
		
		for (int i = 1; i <= _NX; i++) {
			for (int j = 1; j <= _NY; j++) {
				x = i - (dt0_t * du[((i) + (_NX + 2)  *(j))])/dt0_b;
				y = j - (dt0_t * dv[((i) + (_NX + 2)  *(j))])/dt0_b;
				
				if (x > _NX + 1) x = _NX; //ehhh?
				if (x < 1)     x = 1;
				
				i0 = (int) x;
				i1 = i0 + 1;
				
				if (y > _NY + 1) y = _NY; //ehhh?
				if (y < 1)     y = 1;
				
				j0 = (int) y;
				j1 = j0 + 1;
				
				s1 = x - i0;
				s0 = 1 - s1;
				t1 = y - j0;
				t0 = 1 - t1;
				
				r[((i) + (_NX + 2)  *(j))] = s0 * (t0 * rOld[((i0) + (_NX + 2)  *(j0))] + t1 * rOld[((i0) + (_NX + 2)  *(j1))])	+ s1 * (t0 * rOld[((i1) + (_NX + 2)  *(j0))] + t1 * rOld[((i1) + (_NX + 2)  *(j1))]);
				g[((i) + (_NX + 2)  *(j))] = s0 * (t0 * gOld[((i0) + (_NX + 2)  *(j0))] + t1 * gOld[((i0) + (_NX + 2)  *(j1))])	+ s1 * (t0 * gOld[((i1) + (_NX + 2)  *(j0))] + t1 * gOld[((i1) + (_NX + 2)  *(j1))]);			
				b[((i) + (_NX + 2)  *(j))] = s0 * (t0 * bOld[((i0) + (_NX + 2)  *(j0))] + t1 * bOld[((i0) + (_NX + 2)  *(j1))])	+ s1 * (t0 * bOld[((i1) + (_NX + 2)  *(j0))] + t1 * bOld[((i1) + (_NX + 2)  *(j1))]);				
			}
		}
		setBoundaryRGB(bound);
	}
	
	
	
	protected void diffuse(int b, int[] c, int[] c0, int _diff_t, int _diff_b) {
		int a = (_dt_t * _diff_t * _NX * _NY)/(_dt_b*_diff_b);
		Log.d("FluidSimINT", "diffuse, a:"+a);
		linearSolver(b, c, c0, a, 1 + 4 * a);
	}
	
	protected void diffuseRGB(int b, int _diff_t, int _diff_b) {
		int a_t = (_dt_t * _diff_t * _NX * _NY);
		int a_b = (_dt_b * _diff_b);
		Log.d("FluidSimINT", "diffuseRGB, a_t:"+a_t);
		Log.d("FluidSimINT", "diffuseRGB, a_b:"+a_b);
		linearSolverRGB(b, a_t, a_b + 4 * a_t, a_b);
	}
	
	protected void diffuseUV(int b, int _diff_t, int _diff_b) {
		final int a_t = (_dt_t * _diff_t * _NX * _NY);
		final int a_b = (_dt_b*_diff_b);
		Log.d("FluidSimINT", "diffuseUV, a_t:"+a_t);
		Log.d("FluidSimINT", "diffuseUV, a_b:"+a_b);
		linearSolverUV(b, a_t, a_b + 4 * a_t, a_b);
	}
	
	
	protected void project(int[] x, int[] y, int[] p, int[] div)  {
		for (int i = 1; i <= _NX; i++) {
			for (int j = 1; j <= _NY; j++) {
				div[((i) + (_NX + 2)  *(j))] = (x[((i+1) + (_NX + 2)  *(j))] - x[((i-1) + (_NX + 2)  *(j))] + y[((i) + (_NX + 2)  *(j+1))] - y[((i) + (_NX + 2)  *(j-1))])
				/ (_NX * -2);
				p[((i) + (_NX + 2)  *(j))] = 0;
			}
		}
		
		
		linearSolver(0, p, div, 1, 4);
		
		for (int i = 1; i <= _NX; i++) {
			for (int j = 1; j <= _NY; j++) {
				x[((i) + (_NX + 2)  *(j))] -= (_NX * (p[((i+1) + (_NX + 2)  *(j))] - p[((i-1) + (_NX + 2)  *(j))])/2);
				y[((i) + (_NX + 2)  *(j))] -= (_NX * (p[((i) + (_NX + 2)  *(j+1))] - p[((i) + (_NX + 2)  *(j-1))])/2);
			}
		}
		
	}
	
	
	
	protected void linearSolver(int b, int[] x, int[] x0, int a, int c) {
		for (int k = 0; k < _solverIterations; k++) {
			for (int i = 1; i <= _NX; i++) {
				for (int j = 1; j <= _NY; j++) {
					x[((i) + (_NX + 2)  *(j))] = (a * ( x[((i-1) + (_NX + 2)  *(j))] + x[((i+1) + (_NX + 2)  *(j))]  +   x[((i) + (_NX + 2)  *(j-1))] + x[((i) + (_NX + 2)  *(j+1))])  +  x0[((i) + (_NX + 2)  *(j))]) / c;
				}
			}
		}
	}
	
	//#define LINEAR_SOLVE_EQ	(x, x0)			(a * ( x[] + x[]  +  x[] + x[])  +  x0[]) / c;
	
	protected void linearSolverRGB(int bound, int a_t, int c, int a_b) {
		int index1, index2, index3, index4, index5;
		for (int k = 0; k < _solverIterations; k++) {		// MEMO
			for (int i = 1; i <= _NX; i++) {
				for (int j = 1; j <= _NY; j++) {
					index5 = ((i) + (_NX + 2)  *(j));
					index1 = index5 - 1;//FLUID_IX(i-1, j);
					index2 = index5 + 1;//FLUID_IX(i+1, j);
					index3 = index5 - (_NX + 2);//FLUID_IX(i, j-1);
					index4 = index5 + (_NX + 2);//FLUID_IX(i, j+1);
					
					r[index5] = (a_t * ( r[index1] + r[index2]  +  r[index3] + r[index4])  +  rOld[index5]) / (c*a_b);
					g[index5] = (a_t * ( g[index1] + g[index2]  +  g[index3] + g[index4])  +  gOld[index5]) / (c*a_b);
					b[index5] = (a_t * ( b[index1] + b[index2]  +  b[index3] + b[index4])  +  bOld[index5]) / (c*a_b);				
					//				x[FLUID_IX(i, j)] = (a * ( x[FLUID_IX(i-1, j)] + x[FLUID_IX(i+1, j)]  +  x[FLUID_IX(i, j-1)] + x[FLUID_IX(i, j+1)])  +  x0[FLUID_IX(i, j)]) / c;
				}
			}
			setBoundaryRGB(bound);
		}
	}
	
	protected void linearSolverUV(int bound, int a_t, int c, int a_b) {
		int index1, index2, index3, index4, index5;
		for (int k = 0; k < _solverIterations; k++) {		// MEMO
			for (int i = 1; i <= _NX; i++) {
				for (int j = 1; j <= _NY; j++) {
					index5 = ((i) + (_NX + 2)  *(j));
					index1 = index5 - 1;//FLUID_IX(i-1, j);
					index2 = index5 + 1;//FLUID_IX(i+1, j);
					index3 = index5 - (_NX + 2);//FLUID_IX(i, j-1);
					index4 = index5 + (_NX + 2);//FLUID_IX(i, j+1);
					
					u[index5] = (a_t * ( u[index1] + u[index2]  +  u[index3] + u[index4])  +  uOld[index5]) / (c*a_b);
					v[index5] = (a_t * ( v[index1] + v[index2]  +  v[index3] + v[index4])  +  vOld[index5]) / (c*a_b);
					//				x[FLUID_IX(i, j)] = (a * ( x[FLUID_IX(i-1, j)] + x[FLUID_IX(i+1, j)]  +  x[FLUID_IX(i, j-1)] + x[FLUID_IX(i, j+1)])  +  x0[FLUID_IX(i, j)]) / c;
				}
			}
			setBoundaryRGB(bound);
		}
	}
	
	
	
	protected void setBoundary(int b, float[] x) {
	}
	

	protected void setBoundaryRGB(int bound) {
	}
	
	public int FLUID_IX(int i, int j) {
		return ((i) + (_NX + 2)  *(j));
	}
	
	
	protected void swapU() { 
		System.arraycopy(u, 0, _tmp, 0, u.length);
		System.arraycopy(uOld, 0, u, 0, u.length);
		System.arraycopy(_tmp, 0, uOld, 0, u.length);

	}
	protected void swapV(){ 
		
		System.arraycopy(v, 0, _tmp, 0, v.length);
		System.arraycopy(vOld, 0, v, 0, v.length);
		System.arraycopy(_tmp, 0, vOld, 0, v.length);

		
	}
	protected void swapR(){ 
		System.arraycopy(r, 0, _tmp, 0, r.length);
		System.arraycopy(rOld, 0, r, 0, r.length);
		System.arraycopy(_tmp, 0, rOld, 0, r.length);

	}
	
	protected void swapRGB(){ 
		System.arraycopy(r, 0, _tmp, 0, r.length);
		System.arraycopy(rOld, 0, r, 0, r.length);
		System.arraycopy(_tmp, 0, rOld, 0, r.length);

		
		System.arraycopy(g, 0, _tmp, 0, g.length);
		System.arraycopy(gOld, 0, g, 0, g.length);
		System.arraycopy(_tmp, 0, gOld, 0, g.length);

				
		System.arraycopy(b, 0, _tmp, 0, b.length);
		System.arraycopy(bOld, 0, b, 0, b.length);
		System.arraycopy(_tmp, 0, bOld, 0, b.length);

	}

	
	
	final protected int width;
	final protected int height;
	final protected int invWidth;
	final protected int invHeight;
	
	final protected int		_NX, _NY, _numCells;
	protected int	_dt_t;
	protected int	_dt_b;
	protected boolean	_isInited;
	protected boolean	_isRGB;				// for monochrome, only update r
	protected int		_solverIterations;
	
	protected int	visc_t;
	protected int	visc_b;
	protected int	_fadeSpeed_t;
	protected int	_fadeSpeed_b;
	
	protected int[] _tmp;
	
}

