import React from 'react';

export const DocSection: React.FC = () => {
	const code = `export const AppButton = ({ isLoading, variant = "brand", children }) => {
  return (
    <button className="relative rounded-2xl font-bold text-white shadow-[0_4px_0_#bd2d00] active:top-1 active:shadow-none">
      {children}
    </button>
  );
};`;

	const handleCopy = () => {
		navigator.clipboard.writeText(code);
		alert('Đã sao chép code!');
	};

	return (
		<div
			className="bg-slate-900 rounded-3xl p-6 border-2 border-slate-800 shadow-[0_6px_0_#0f172a] text-white space-y-4">
			<div className="flex justify-between items-center border-b border-slate-800 pb-3">
        <span className="text-xs font-mono text-orange-400">
          <i className="fa-solid fa-code mr-2"></i>AppButton.tsx
        </span>
				<button
					onClick={handleCopy}
					className="text-xs font-bold bg-slate-800 px-3 py-1.5 rounded-xl border border-slate-700 hover:bg-slate-700 transition-all"
				>
					<i className="fa-regular fa-copy mr-1"></i> Copy Code
				</button>
			</div>
			<pre className="text-xs font-mono text-slate-300 overflow-x-auto">
        <code>{code}</code>
      </pre>
		</div>
	);
};