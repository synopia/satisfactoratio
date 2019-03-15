var path = require("path");
var SpriteSmithPlugin = require("webpack-spritesmith");
config.plugins.push(
    new SpriteSmithPlugin({
        src: {
            cwd: path.resolve(__dirname, '../src/main/images'),
            glob: '*.png'
        },
        target: {
            image: path.resolve(__dirname, '../src/main/web/sprite.png'),
            css: path.resolve(__dirname, '../src/main/web/sprite.css')
        },
        apiOptions: {
            cssImageRef: "sprite.png"
        }
    })
);