precision mediump float;
uniform sampler2D u_TextureUnit;
uniform sampler2D u_TextureUnit2;

varying vec2 v_TextureCoordinates;

void main() {
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}