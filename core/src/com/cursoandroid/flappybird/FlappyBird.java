package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaro;
    private Texture fundo;
    private Texture canoBaixo, canoTopo, gameOver;
    private BitmapFont fonte, mensagem;
    private Circle passaroCirculo;
    private Rectangle canoTopoRetangulo, canoBaixoRetangulo;
    //private ShapeRenderer shapeRenderer;*/

    private float variacao = 0;
    private float larguraDispositivo;
    private float alturaDispositivo;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private Random numericoRandomico;
    private float alturaEntreCanosRandomico;
    private int estadoJogo = 0;
    private int pontuacao = 0;
	private boolean marcouPonto = false;

    //camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {

        batch = new SpriteBatch();
        passaro = new Texture[3];

        passaroCirculo = new Circle();
        canoBaixoRetangulo = new Rectangle();
        canoTopoRetangulo = new Rectangle();

        /*shapeRenderer = new ShapeRenderer();*/

        passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");

        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");

        gameOver = new Texture("game_over.png");

        numericoRandomico = new Random();

        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

        //configurações da camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;


        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 300;
	}

	@Override
	public void render () {

        camera.update();

        //Limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        variacao += Gdx.graphics.getDeltaTime() * 10;

        if (variacao > 2) variacao = 0;

        if(estadoJogo == 0){ // Jogo nao inicializado
            if(Gdx.input.justTouched()){
                estadoJogo = 1;
            }
        }else {// Jogo Inicializado


            velocidadeQueda++;

            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical -= velocidadeQueda;


            if(estadoJogo == 1){

                posicaoMovimentoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;

                if (Gdx.input.justTouched()) {

                    velocidadeQueda = -15;
                }

                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomico = numericoRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                if(posicaoMovimentoCanoHorizontal < 120){
                    if(!marcouPonto){
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            }else{
                if(Gdx.input.justTouched()){

                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                }

            }
        }

        //configurar dados de projeção da camera
        batch.setProjectionMatrix( camera.combined );

        batch.begin();

        batch.draw(fundo,0,0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomico);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomico);
        batch.draw(passaro[(int)variacao],120, posicaoInicialVertical);

        if(estadoJogo == 2){

            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2 , alturaDispositivo / 2);
            mensagem.draw(batch, "Toque para Reiniciar!", larguraDispositivo / 2 - 200, alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo - 50);

        batch.end();

        passaroCirculo.set(120+passaro[0].getWidth()/2,posicaoInicialVertical+passaro[0].getHeight()/2,passaro[0].getWidth()/2);
        canoBaixoRetangulo.set(posicaoMovimentoCanoHorizontal,
                alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomico,
                canoBaixo.getWidth(),canoBaixo.getHeight());
        canoTopoRetangulo.set(posicaoMovimentoCanoHorizontal,
                alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomico,
                canoTopo.getWidth(),canoTopo.getHeight());

        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(passaroCirculo.x,passaroCirculo.y,passaroCirculo.radius);
        shapeRenderer.rect(canoBaixoRetangulo.x,canoBaixoRetangulo.y,canoBaixoRetangulo.getWidth(),canoBaixoRetangulo.getHeight());
        shapeRenderer.rect(canoTopoRetangulo.x,canoTopoRetangulo.y,canoTopoRetangulo.getWidth(),canoTopoRetangulo.getHeight());
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.end();*/

        //teste coliçao
        if(Intersector.overlaps(passaroCirculo, canoBaixoRetangulo) || Intersector.overlaps(passaroCirculo,canoTopoRetangulo)
                || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo){

            estadoJogo = 2;
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
