package com.example.ggunnssakac.cube2_returnofthecube;

/**
 * Created by ggunn on 21/11/14.
 */
public interface IShaderProvider {

    public String getVertexShader();
    public String getFragmentShader();
    public String getVertexShader(int resId);
    public String getFragmentShader(int resId);
}
