# reversi
コンソールで遊ぶリバーシゲームです。  
[初版](https://github.com/nmby/reversi) からリニューアルしました。  

人間とコンピュータや、コンピュータ同士で対戦を行えます。  
コンピュータ同士の総当たり戦を自動で行い、対戦成績を一覧で表示することもできます。  

次の標準AIプレーヤーがパッケージに含まれています。  
- SimplestAIPlayer：盤上を左上から順に走査するAIプレーヤーです。
- RandomAIPlayer：ランダムに手を選択するAIプレーヤーです。
- DepthFirstAIPlayer：深さ優先探索で必勝手を探すAIプレーヤーです。
- BreadthFirstAIPlayer：幅優先探索で最善手を探すAIプレーヤーです。
- MonteCarloAIPlayer：モンテカルロ・シミュレーションにより最善手を探すAIプレーヤーです。

AIプレーヤーを自作することも簡単です。  
[Playerインタフェース](https://nmby.github.io/reversi2/xyz/hotchpotch/reversi/core/Player.html) を実装し、
[Player#decide](https://nmby.github.io/reversi2/xyz/hotchpotch/reversi/core/Player.html#decide(xyz.hotchpotch.reversi.core.Board,xyz.hotchpotch.reversi.core.Color,long)) メソッドをオーバーライドするだけです。  
自作したAIプレーヤーと標準AIプレーヤーを対戦させることもできます。  

詳細は [こちらの紹介サイト](https://reversi.hotchpotch.xyz/) および [javadoc](https://nmby.github.io/reversi2/) をご参照ください。  

## ライセンス
MIT License に基づいて公開しています。  
詳細は LICENSE.txt ファイルをご参照ください。  

## 連絡先
e-mail : nmby@hotchpotch.xyz  
website : https://reversi.hotchpotch.xyz/  
